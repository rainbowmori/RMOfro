package github.rainbowmori.ofro.object.config.rtp;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.object.config.rtp.world.ConfigRTPWorld;
import github.rainbowmori.ofro.object.config.rtp.world.RTPWorld;
import github.rainbowmori.rainbowapi.api.ConfigAPI;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public final class RTPConfig extends ConfigAPI {

  private final RTPWorld defaultWorld;
  private final Map<String, RTPWorld> worlds = new HashMap<>();

  public final RTPMessage SuccessMessage;
  public final RTPMessage StartMessage;
  public final RTPMessage FailedMessage;
  public final RTPMessage AlreadyMessage;

  public final int maxAttempts;


  public RTPConfig() {
    super(Ofro.getPlugin(), "RTP.yml");

    this.maxAttempts = data.getInt("MaxAttempts", 50);

    this.defaultWorld = new ConfigRTPWorld(data.getConfigurationSection("DefaultWorld"));

    ConfigurationSection worlds = data.getConfigurationSection("Worlds");

    if (worlds != null) {
      for (String worldName : worlds.getKeys(false)) {
        this.worlds.put(worldName, new ConfigRTPWorld(worlds.getConfigurationSection(worldName)));
      }
    }

    ConfigurationSection titles = data.getConfigurationSection("Titles");
    if (titles == null) {
      SuccessMessage = new RTPMessage("<red>ランダムな場所にテレポート!!!", "<white>x = {x} y = {y} z = {z}",
          "{player}は<white>{world}のx = {x} y = {y} z = {z}にテレポートしました", 3, true);
      StartMessage = new RTPMessage("<gold>Teleporting...", "<dark_gray>please wait",
          "<gold>{player}のテレポートを開始します", 3, false);
      FailedMessage = new RTPMessage("<red>失敗です!安全な場所はありませんでした", "",
          "<red>{player}のrtpに失敗しました", 3, true);
      AlreadyMessage = new RTPMessage("<red>あなたはrtp実行中です", "<red>待っていてください",
          "<red>{player}はすでにrtp実行中です待っていてください", 3, true);
    } else {
      SuccessMessage =
          titles.contains("Success") ? new RTPMessage(titles.getConfigurationSection("Success"))
              : new RTPMessage("<red>ランダムな場所にテレポート!!!", "<white>x = {x} y = {y} z = {z}",
                  "{player}は<white>{world}のx = {x} y = {y} z = {z}にテレポートしました", 3, true);
      StartMessage =
          titles.contains("Start") ? new RTPMessage(titles.getConfigurationSection("Start"))
              : new RTPMessage("<gold>Teleporting...", "<dark_gray>please wait",
                  "<gold>{player}のテレポートを開始します", 3, false);
      FailedMessage =
          titles.contains("Failed") ? new RTPMessage(titles.getConfigurationSection("Failed"))
              : new RTPMessage("<red>失敗です!安全な場所はありませんでした", "",
                  "<red>{player}のrtpに失敗しました", 3, true);
      AlreadyMessage =
          titles.contains("Already") ? new RTPMessage(titles.getConfigurationSection("Already"))
              : new RTPMessage("<red>あなたはrtp実行中です", "<red>待っていてください",
                  "<red>{player}はすでにrtp実行中です待っていてください", 3, true);
    }
  }

  public RTPWorld getRTPWorld(String world) {
    return worlds.getOrDefault(world, defaultWorld);
  }

}
