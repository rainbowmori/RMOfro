package github.rainbowmori.ofro.object.savage;

import github.rainbowmori.ofro.constants.OfroPrefix;
import java.util.Optional;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SavagePlayer {

  private Location bedLoc;

  private boolean isDeletion;

  public boolean isDeletion() {
    return isDeletion;
  }

  public void setDeletion(boolean deletion) {
    isDeletion = deletion;
  }

  public Optional<Location> getOptionalLocation() {
    return Optional.ofNullable(bedLoc);
  }

  public void setBedLoc(Location bedLoc) {
    this.bedLoc = bedLoc;
  }

  public boolean teleportBed(Player player) {
    if (bedLoc == null) {
      return false;
    }
    if (!bedLoc.getBlock().getType().name().endsWith("_BED")) {
      return false;
    }
    PaperLib.teleportAsync(player, bedLoc).thenRun(
        () -> OfroPrefix.TP.send(player, "<green>テレポート地点が設定されていたため、ベッドにテレポートしました！"));
    return true;
  }
}
