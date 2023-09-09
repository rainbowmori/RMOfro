package github.rainbowmori.ofro.object.config;

import com.google.gson.reflect.TypeToken;
import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.rainbowapi.RainbowAPI;
import github.rainbowmori.rainbowapi.api.JsonAPI;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerDataConfig extends JsonAPI {

  private static final Type typeRmtSellPrices = new TypeToken<Map<UUID, Integer>>() {}.getType();

  private final Map<UUID, Integer> rmtSellPrices;

  public boolean hasRMTSell(UUID uuid) {
    return rmtSellPrices.containsKey(uuid) && getRMTSell(uuid) != 0;
  }

  public void addRMTSell(UUID uuid, int price) {
    rmtSellPrices.put(uuid, getRMTSell(uuid) + price);
  }

  public int getRMTSell(UUID uuid) {
    return rmtSellPrices.getOrDefault(uuid, 0);
  }

  public void resetRMTSell(UUID uuid) {
    rmtSellPrices.remove(uuid);
  }

  public PlayerDataConfig() {
    super(Ofro.getPlugin(), "playerdata");
    rmtSellPrices = RainbowAPI.gson.fromJson(getCreateJsonObject(getData(),List.of("data")).get("rmtSellPrices"),typeRmtSellPrices);
  }

  @Override
  public void saveFile() {
    getCreateJsonObject(getData(), List.of("data")).add("rmtSellPrices",RainbowAPI.gson.toJsonTree(rmtSellPrices));
    super.saveFile();
  }
}
