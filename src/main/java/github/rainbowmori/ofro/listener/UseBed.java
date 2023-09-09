package github.rainbowmori.ofro.listener;

import github.rainbowmori.ofro.object.savage.SavageWorld;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class UseBed implements Listener {

  @EventHandler
  public void use(PlayerBedEnterEvent e) {
    Block bed = e.getBed();
    World world = bed.getWorld();
    if (SavageWorld.isSavageWorld(world) && world.isBedWorks()) {
      SavageWorld.getSavageWorld(world)
          .getSavagePlayer(e.getPlayer().getUniqueId()).setBedLoc(bed.getLocation());
    }
  }

}
