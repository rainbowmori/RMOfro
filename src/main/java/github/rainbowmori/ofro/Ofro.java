package github.rainbowmori.ofro;

import github.rainbowmori.ofro.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ofro extends JavaPlugin {

  private static ConfigManager configManager;

  public static ConfigManager getConfigManager() {
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
    configManager = new ConfigManager();
    saveResource("README.txt", true);
  }

  @Override
  public void onDisable() {
  }
}
