package github.rainbowmori.ofro

import github.rainbowmori.ofro.config.ConfigManager
import org.bukkit.plugin.java.JavaPlugin

class Ofro : JavaPlugin() {
  override fun onEnable() {
    configManager = ConfigManager()
    saveResource("README.txt", true)
  }

  // kotlin の neovim setup をしよう

  override fun onDisable() {}

  companion object {
    @JvmStatic
    var configManager: ConfigManager? = null
      private set

    fun reloadConfigManager() {
      configManager!!.save()
      configManager = ConfigManager()
    }

    val plugin: Ofro
      get() = getPlugin(Ofro::class.java)
  }
}
