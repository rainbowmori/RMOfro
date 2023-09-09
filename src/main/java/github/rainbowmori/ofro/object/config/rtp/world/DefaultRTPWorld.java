package github.rainbowmori.ofro.object.config.rtp.world;

public class DefaultRTPWorld implements RTPWorld {

  private static final DefaultRTPWorld INSTANCE = new DefaultRTPWorld();

  public static DefaultRTPWorld getInstance() {
    return INSTANCE;
  }

  private DefaultRTPWorld() {

  }

  @Override

  public boolean getCenterOfWorldSpawn() {
    return true;
  }

  @Override
  public int getMaxRadius() {
    return 1000;
  }

  @Override
  public int getMinRadius() {
    return 10;
  }

  @Override
  public int getCenterX() {
    return 0;
  }

  @Override
  public int getCenterZ() {
    return 0;
  }

  @Override
  public int getMaxY() {
    return 320;
  }

  @Override
  public int getMinY() {
    return 0;
  }
}
