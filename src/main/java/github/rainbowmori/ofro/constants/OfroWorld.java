package github.rainbowmori.ofro.constants;

import github.rainbowmori.ofro.Ofro;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * lobbyとかcasualとかそこらへんの不変的なワールド
 */
public class OfroWorld {

  @NotNull
  public static final World LOBBY = Objects.requireNonNull(Bukkit.getWorld("newworld"));

  @NotNull
  public static final World SHOP = Objects.requireNonNull(Bukkit.getWorld("shop"));

  public static void teleportLobby(Player player) {
    player.teleport(Ofro.getCore().getMVWorldManager().getMVWorld(LOBBY.getName()).getSpawnLocation());
  }

  public static boolean isLobby(World world) {
    return LOBBY.equals(world);
  }


}
