package github.rainbowmori.ofro;

import org.bukkit.plugin.java.JavaPlugin;

import github.rainbowmori.ofro.config.ConfigManager;
import github.rainbowmori.ofro.listeners.MVTeleportEvents;

public class Ofro extends JavaPlugin {

  private static ConfigManager configManager;

  public static ConfigManager getConfigService() {
    return configManager;
  }

  public static void reloadConfigManager() {
    configManager.save();
    configManager = new ConfigManager();
  }

  public static Ofro getPlugin() {
    return getPlugin(Ofro.class);
  }

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new MVTeleportEvents(), this);
  }
}
