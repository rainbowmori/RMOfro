package github.rainbowmori.ofro.listener;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.Items;
import github.rainbowmori.ofro.constants.OfroWorld;
import github.rainbowmori.ofro.constants.Savage;
import github.rainbowmori.ofro.object.savage.SavageWorld;
import github.rainbowmori.rainbowapi.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EventListeners implements Listener {

  @EventHandler
  public void move(PlayerMoveEvent e) {
    Player player = e.getPlayer();
    World world = player.getWorld();
    if (OfroWorld.isLobby(world) && player.getLocation().getY() < 50) {
      player.sendActionBar(Util.mm("<red><bold>奈落に落ちたため、ロビーにテレポートしました。"));
      OfroWorld.teleportLobby(player);
    }
  }

  @EventHandler
  public void breakBlock(BlockBreakEvent e) {
    World world = e.getBlock().getWorld();
    if (isCancelable(world, e.getPlayer())) {
      e.setCancelled(true);
      return;
    }
  }

  @EventHandler
  public void placeBlock(BlockPlaceEvent e) {
    World world = e.getBlock().getWorld();
    if (isCancelable(world, e.getPlayer())) {
      e.setCancelled(true);
      return;
    }
  }

  public boolean isCancelable(World world, Player player) {
    GameMode gameMode = player.getGameMode();
    if (player.isOp() && (gameMode.equals(GameMode.CREATIVE) || gameMode.equals(
        GameMode.SPECTATOR))) {
      return false;
    }
    return !SavageWorld.isSavageWorld(world);
  }

  @EventHandler
  public void respawn(PlayerRespawnEvent e) {
    Player player = e.getPlayer();
    Bukkit.getScheduler().runTask(Ofro.getPlugin(), () -> OfroWorld.teleportLobby(player));
  }

  @EventHandler
  public void death(PlayerDeathEvent e) {
    Player player = e.getEntity();
    if (SavageWorld.isSavageWorld(player.getWorld())) {
      Savage.dropMoney(player, player.getLocation());
    }
  }

  @EventHandler
  public void pickUp(PlayerAttemptPickupItemEvent e) {
    Player player = e.getPlayer();
    Item item = e.getItem();
    if (SavageWorld.isSavageWorld(player.getWorld())) {
      ItemStack itemStack = item.getItemStack();
      if (Items.isMoneyItem(itemStack)) {
        e.setCancelled(true);
        item.remove();
        player.playSound(player,Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
        int money = Items.getMoneyOfItem(itemStack);
        Util.send(player, "<yellow>あなたは" + money + "円を拾いました!");
        Ofro.getEconomy().depositPlayer(player, money);
      }
    }
  }

  @EventHandler
  public void damage(EntityDamageByEntityEvent e) {
    World world = e.getDamager().getWorld();
    if (!world.equals(OfroWorld.LOBBY) && !world.equals(OfroWorld.SHOP)) {
      return;
    }
    if (!(e.getDamager() instanceof Player attacker)) {
      return;
    }
    if (!(e.getEntity() instanceof Player victim)) {
      return;
    }
    e.setCancelled(true);
    attacker.sendActionBar(Util.mm("<red><bold>PVPは許可されていません!"));
  }
}
