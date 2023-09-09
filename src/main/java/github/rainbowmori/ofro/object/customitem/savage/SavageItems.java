package github.rainbowmori.ofro.object.customitem.savage;

import github.rainbowmori.rainbowapi.object.cache.CacheData;
import java.util.UUID;

public class SavageItems implements CacheData<UUID> {

  private final UUID uuid;

  public SavageItems(UUID uuid) {
    this.uuid = uuid;
  }

  public boolean isSonerUsed;

  @Override
  public UUID getKey() {
    return uuid;
  }
}
