package github.rainbowmori.ofro.object.config;

import github.rainbowmori.ofro.object.config.rtp.RTPConfig;

public class ConfigService {

  public final DestinationConfig DESTINATION = new DestinationConfig();
  public final RTPConfig RTP = new RTPConfig();
  public final MessageConfig MESSAGE = new MessageConfig();
  public final PlayerDataConfig PLAYERDATA = new PlayerDataConfig();

  public void save() {
    PLAYERDATA.saveFile();
  }
}
