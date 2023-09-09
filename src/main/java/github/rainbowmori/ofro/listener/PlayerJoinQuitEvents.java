package github.rainbowmori.ofro.listener;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.OfroWorld;
import github.rainbowmori.ofro.constants.Savage;
import github.rainbowmori.ofro.object.manager.ManagerService;
import github.rainbowmori.ofro.object.manager.QuitPlayer;
import github.rainbowmori.ofro.object.savage.SavageWorld;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinQuitEvents implements Listener {

  public PlayerJoinQuitEvents() {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    protocolManager.addPacketListener(new PacketAdapter(Ofro.getPlugin(), Client.USE_ENTITY) {
          @Override
          public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            Integer entityID = packet.getIntegers().read(0);
            ManagerService managerService = Ofro.getManagerService();
            if (!managerService.isQuitPlayer(uuid)) {
              return;
            }
            WrappedEnumEntityUseAction read = packet.getEnumEntityUseActions().read(0);
            try {
              read.getHand();
            } catch (Exception e) {
              QuitPlayer quitPlayer = managerService.getQuitPlayer(uuid);
              managerService.removeQuitPlayer(uuid);
              PacketContainer destroy = protocolManager
                  .createPacket(Server.ENTITY_DESTROY);
              destroy.getIntLists().write(0, List.of(entityID));
              PacketContainer remove = protocolManager
                  .createPacket(Server.PLAYER_INFO_REMOVE);
              remove.getUUIDLists().write(0, Collections.singletonList(quitPlayer.npcUUID()));
              Location location = quitPlayer.location();
              World world = location.getWorld();
              SavageWorld.getSavageWorld(world).getSavagePlayer(quitPlayer.playerUUID()).setDeletion(true);
              protocolManager.broadcastServerPacket(destroy);
              protocolManager.broadcastServerPacket(remove);
              Bukkit.getScheduler().runTask(Ofro.getPlugin(), () -> {
                Savage.dropMoney(Bukkit.getOfflinePlayer(quitPlayer.playerUUID()),location);
                for (ItemStack itemStack : quitPlayer.itemStacks()) {
                  if (itemStack == null) {
                    continue;
                  }
                  world.dropItemNaturally(location, itemStack);
                }
              });
            }
          }
        });
  }

  @EventHandler
  public void login(PlayerLoginEvent e) {
    Player player = e.getPlayer();
    UUID uniqueId = player.getUniqueId();
    ManagerService managerService = Ofro.getManagerService();
    if (managerService.isQuitPlayer(uniqueId)) {
      e.disallow(Result.KICK_OTHER,Util.mm("あなたはsavage worldで抜けているため%s秒参加できません".formatted(managerService.getQuitTime(uniqueId))));
      return;
    }
  }

  @EventHandler
  public void join(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    OfroWorld.teleportLobby(player);
  }

  @EventHandler
  public void quit(PlayerQuitEvent e) {
    Player player = e.getPlayer();
    UUID uniqueId = player.getUniqueId();
    if (SavageWorld.isSavageWorld(e.getPlayer().getWorld())) {
      ManagerService managerService = Ofro.getManagerService();
      if (!managerService.isQuitPlayer(uniqueId)) {
        managerService.putQuitPlayer(player);
        return;
      }
    }
  }
}
