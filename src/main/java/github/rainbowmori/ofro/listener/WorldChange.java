package github.rainbowmori.ofro.listener;

import github.rainbowmori.ofro.object.savage.SavagePlayer;
import github.rainbowmori.ofro.object.savage.SavageWorld;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {

  @EventHandler
  public void change(PlayerChangedWorldEvent e) {
    Player player = e.getPlayer();
    World world = player.getWorld();
    String worldName = world.getName();
    if (SavageWorld.isSavageWorld(worldName)) {
      UUID uuid = player.getUniqueId();
      SavagePlayer savagePlayer = SavageWorld.getSavageWorld(world)
          .getSavagePlayer(uuid);
      if (savagePlayer.isDeletion()) {
        savagePlayer.setDeletion(false);
        player.getInventory().clear();
      }
    }
    String sendWorldName = switch (worldName) {
      case "newworld" -> "lobby";
      default -> worldName;
    };
    player.sendActionBar(Util.mm("<gold><bold>現在のワールド:<yellow>" + sendWorldName));
    player.playSound(player,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
    player.playSound(player,Sound.ENTITY_PLAYER_LEVELUP,1,1);
  }
}
