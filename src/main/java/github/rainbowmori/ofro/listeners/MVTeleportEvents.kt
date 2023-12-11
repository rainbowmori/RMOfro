package github.rainbowmori.ofro.listeners

import com.onarandombox.MultiverseCore.event.MVTeleportEvent
import com.onarandombox.MultiversePortals.event.MVPortalEvent
import github.rainbowmori.ofro.Ofro.Companion.configManager
import github.teamofro.ofrolib.multiverse.CommandDestination
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MVTeleportEvents : Listener {
  @EventHandler
  fun onPortal(event: MVPortalEvent) {
    val dest = event.destination as? CommandDestination ?: return
    configManager!!.destinationConfig.runCommand(event.teleportee, dest.name)
    event.isCancelled = true
  }

  @EventHandler
  fun onTeleport(event: MVTeleportEvent) {
    val dest = event.destination as? CommandDestination ?: return
    configManager!!.destinationConfig.runCommand(event.teleportee, dest.name)
    event.isCancelled = true
  }
}
