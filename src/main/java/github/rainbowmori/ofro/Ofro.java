package github.rainbowmori.ofro;

import org.bukkit.plugin.java.JavaPlugin;

public final class Ofro extends JavaPlugin {

  @Override
  public void onEnable() {
    saveResource("README.txt", true);
  }

  @Override
  public void onDisable() {
  }
}
