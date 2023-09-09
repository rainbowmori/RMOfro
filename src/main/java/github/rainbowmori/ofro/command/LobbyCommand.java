package github.rainbowmori.ofro.command;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.ofro.constants.OfroWorld;
import github.rainbowmori.rainbowapi.dependencies.commandapi.CommandAPICommand;
import github.rainbowmori.rainbowapi.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyCommand extends CommandAPICommand {

  public LobbyCommand() {
    super("lobby");
    executesPlayer((sender, args) -> {
      GameMode gameMode = sender.getGameMode();
      if (sender.isOp() && (gameMode.equals(GameMode.CREATIVE) || gameMode.equals(
          GameMode.SPECTATOR))) {
        OfroWorld.teleportLobby(sender);
        OfroPrefix.TP.send(sender, "<green>ロビーにテレポートしました!");
        sender.playSound(sender,Sound.ENTITY_PLAYER_LEVELUP,1,1);
        return;
      }
      new BukkitRunnable() {
        final Location pLoc = sender.getLocation();
        int attempts = 3;

        @Override
        public void run() {
          if (!sender.getLocation().equals(pLoc)) {
            sender.sendActionBar(Util.mm("<red><bold>テレポートがキャンセルされました!"));
            cancel();
            return;
          }
          if (attempts == 0) {
            OfroWorld.teleportLobby(sender);
            sender.playSound(sender,Sound.ENTITY_PLAYER_LEVELUP,1,1);
            OfroPrefix.TP.send(sender, "<green>ロビーにテレポートしました!");
            cancel();
            return;
          }
          sender.sendActionBar(Util.mm("<green><bold>テレポートまで…<yellow><reset> " + attempts
              + "<gray> - <red><bold>動かずにお待ちください"));
          sender.playSound(sender,Sound.ENTITY_PLAYER_LEVELUP,1,1);
          attempts--;
        }
      }.runTaskTimer(Ofro.getPlugin(), 0L, 20L);
    });
  }
}
