package github.rainbowmori.ofro

import github.rainbowmori.ofro.config.ConfigManager
import github.rainbowmori.ofro.listeners.MVTeleportEvents
import org.bukkit.plugin.java.JavaPlugin

class Ofro : JavaPlugin() {
  override fun onEnable() {
    configManager = ConfigManager()
    saveResource("README.txt", true)
    server.pluginManager.registerEvents(MVTeleportEvents(),this)
  }

  override fun onDisable() {}

  companion object {
    @JvmStatic
    lateinit var configManager: ConfigManager
      private set

    fun reloadConfigManager() {
      configManager.save()
      configManager = ConfigManager()
    }

    val plugin: Ofro
      get() = getPlugin(Ofro::class.java)
  }
}
