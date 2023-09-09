package github.rainbowmori.ofro.object.config;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.rainbowapi.api.ConfigAPI;

public class MessageConfig extends ConfigAPI {

  public final String SAVAGE_DROP_MONEY;

  public MessageConfig() {
    super(Ofro.getPlugin(), "message.yml");
    SAVAGE_DROP_MONEY = get("SAVAGE_DROP_MONEY", "<gray>{player}は{money}円を落とした");
  }


  public final String get(String key,String defaultValue) {
    return data.getString(key, defaultValue);
  }

}
