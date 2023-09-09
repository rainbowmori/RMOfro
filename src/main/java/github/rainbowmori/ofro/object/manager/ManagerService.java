package github.rainbowmori.ofro.object.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

public class ManagerService {

  private final Map<UUID,QuitPlayer> quitPlayerMap = new HashMap<>();

  public boolean isQuitPlayer(UUID uuid) {
    return quitPlayerMap.containsKey(uuid);
  }

  public QuitPlayer getQuitPlayer(UUID uuid) {
    return quitPlayerMap.get(uuid);
  }

  public void removeQuitPlayer(UUID uuid) {
    quitPlayerMap.remove(uuid);
  }

  public void putQuitPlayer(Player player) {
    UUID uuid = player.getUniqueId();
    QuitPlayer quitPlayer = new QuitPlayer(player);
    quitPlayerMap.put(uuid, quitPlayer);
  }

  public int getQuitTime(UUID playerUUID) {
    return Optional.ofNullable(getQuitPlayer(playerUUID)).map(
            quitPlayer -> 10 - (int) ((System.currentTimeMillis() - quitPlayer.currentTime()) / 1000.0))
        .orElse(0);
  }


}
