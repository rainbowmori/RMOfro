package github.rainbowmori.ofro.listener;

import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import com.onarandombox.MultiversePortals.event.MVPortalEvent;
import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.object.multiverse.CommandDestination;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MVTeleportEvents implements Listener {

  @EventHandler
  public void onPortal(MVPortalEvent event) {
    MVDestination dest = event.getDestination();
    if (!(dest instanceof CommandDestination)) {
      return;
    }
    Ofro.getConfigService().DESTINATION.runCommand(event.getTeleportee(), dest.getName());
    event.setCancelled(true);
  }

  @EventHandler
  public void onTeleport(MVTeleportEvent event) {
    MVDestination dest = event.getDestination();
    if (!(dest instanceof CommandDestination)) {
      return;
    }
    Ofro.getConfigService().DESTINATION.runCommand(event.getTeleportee(), dest.getName());
    event.setCancelled(true);
  }
}
