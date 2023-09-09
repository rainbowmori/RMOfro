package github.rainbowmori.ofro.object.config.rtp.world;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigRTPWorld implements RTPWorld{

  private final boolean centerOfWorldSpawn;

  private final int maxRadius;
  private final int minRadius;

  private final int centerX;
  private final int centerZ;

  private final int maxY;
  private final int minY;

  public ConfigRTPWorld(ConfigurationSection section) {
    DefaultRTPWorld instance = DefaultRTPWorld.getInstance();
    if (section == null) {
      this.maxRadius = instance.getMaxRadius();
      this.minRadius = instance.getMinRadius();
      this.maxY = instance.getMaxY();
      this.minY = instance.getMinY();
      this.centerX = instance.getCenterX();
      this.centerZ = instance.getCenterZ();
      this.centerOfWorldSpawn = instance.getCenterOfWorldSpawn();
      return;
    }
    this.maxRadius = section.getInt("MaxRadius", instance.getMaxRadius());
    this.minRadius = section.getInt("MinRadius", instance.getMinRadius());
    this.maxY = section.getInt("MaxY", instance.getMaxY());
    this.minY = section.getInt("MinY", instance.getMinY());
    this.centerX = section.getInt("CenterX", instance.getCenterX());
    this.centerZ = section.getInt("CenterZ", instance.getCenterZ());
    this.centerOfWorldSpawn = section.getBoolean("CenterOfWorldSpawn", instance.getCenterOfWorldSpawn());
  }

  @Override
  public boolean getCenterOfWorldSpawn() {
    return centerOfWorldSpawn;
  }

  @Override
  public int getMaxRadius() {
    return maxRadius;
  }

  @Override
  public int getMinRadius() {
    return minRadius;
  }

  @Override
  public int getCenterX() {
    return centerX;
  }

  @Override
  public int getCenterZ() {
    return centerZ;
  }

  @Override
  public int getMaxY() {
    return maxY;
  }

  @Override
  public int getMinY() {
    return minY;
  }
}
