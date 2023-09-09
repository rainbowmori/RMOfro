package github.rainbowmori.ofro.object.savage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.rainbowapi.RainbowAPI;
import github.rainbowmori.rainbowapi.api.JsonAPI;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class SavageWorld extends JsonAPI {

  private static final Type type = new TypeToken<Map<UUID, SavagePlayer>>(){}.getType();

  public static final String savageName = "savage";
  public static final Map<String, SavageWorld> savageWorldMap = new HashMap<>();

  public final Map<UUID, SavagePlayer> savagePlayer;
  public final World world;

  public SavageWorld(@NotNull World world) {
    super(Ofro.getPlugin(),world.getName(),"savage");
    if (!isSavageWorld(world)) {
      throw new RuntimeException(world.getName() + "はsavageワールドではありません");
    }
    this.world = world;
    this.savagePlayer = RainbowAPI.gson.fromJson(
        getCreateJsonObject(getData(), List.of("players")), type);
    savageWorldMap.put(world.getName(), this);
  }

  public static String getNaturalWorld(String world) {
    if (world.endsWith("_nether")) {
      return world.replace("_nether", "");
    }
    //endはまだ未定
    /*if (world.endsWith("_end")) {
      return world.replace("_end", "");
    }*/
    return world;
  }

  public static World getNaturalWorld(World world) {
    return Bukkit.getWorld(getNaturalWorld(world.getName()));
  }

  public static boolean isSavageWorld(String worldName) {
    return worldName.startsWith(savageName);
  }

  public static boolean isSavageWorld(World world) {
    return isSavageWorld(world.getName());
  }

  public static SavageWorld getSavageWorld(World world) {
    if (world == null) {
      return null;
    }
    String worldName = world.getName();
    if (!isSavageWorld(worldName)) {
      return null;
    }
    if (savageWorldMap.containsKey(worldName)) {
      return savageWorldMap.get(worldName);
    }
    return new SavageWorld(world);
  }

  public static void saves() {
    savageWorldMap.forEach((s, savageWorld) -> savageWorld.saveFile());
  }

  public SavagePlayer getSavagePlayer(UUID uuid) {
    if (!savagePlayer.containsKey(uuid)) {
      savagePlayer.put(uuid, new SavagePlayer());
    }
    return savagePlayer.get(uuid);
  }

  @Override
  public JsonElement getSavaData() {
    JsonObject object = new JsonObject();
    object.add("players",RainbowAPI.gson.toJsonTree(savagePlayer));
    return object;
  }

  public void remove() {
    savageWorldMap.remove(world.getName());
    removeFile();
  }

}
