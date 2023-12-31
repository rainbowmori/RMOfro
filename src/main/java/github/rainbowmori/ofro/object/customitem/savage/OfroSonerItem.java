package github.rainbowmori.ofro.object.customitem.savage;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.object.cutomitem.cooldown.CooldownItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class OfroSonerItem extends CustomItem implements CooldownItem {

  private static final String stick = "|||";

  public OfroSonerItem() {
    this(new ItemBuilder(Material.COMPASS).changeMeta(
            (Consumer<CompassMeta>) meta -> meta.setLodestoneTracked(false)).name("<red>OfroSoner")
        .build());
  }

  public OfroSonerItem(ItemStack item) {
    super(item);
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();
    if (CooldownItem.hasCooldown(uuid, "OfroAlertItem")) {
      Ofro.getPlugin().getPrefixUtil().send(player, "<red>OfroAlertと同時使用はできません");
      return;
    }
    SavageItems savageItems = CustomItem.itemCache.computeGetCache(uuid, new SavageItems(uuid));
    if (hasCooldown(uuid)) {
      Ofro.getPlugin().getPrefixUtil()
          .send(player, savageItems.isSonerUsed ? "<red>すでにOfroSonerを使用しています"
              : "<red>OfroSonerのクールダウン中です");
      return;
    }
    if (savageItems.isSonerUsed) {
      return;
    }
    savageItems.isSonerUsed = true;
    addCooldown(uuid, 180);
    Util.send(player, "<green>OfroSonerの機能が開始しました");
  }

  @Override
  public @NotNull Optional<String> getActionBarMessage(Player player) {
    UUID uuid = player.getUniqueId();
    SavageItems savageItems = CustomItem.itemCache.computeGetCache(uuid, new SavageItems(uuid));
    if (hasCooldown(uuid)) {
      return savageItems.isSonerUsed ? getBar(uuid) : Optional.of(getHasCooldownMessage(uuid));
    }
    if (savageItems.isSonerUsed) {
      savageItems.isSonerUsed = false;
      addCooldown(uuid, 600);
    }
    return Optional.of(getReadyMessage(uuid));
  }

  private Optional<String> getBar(UUID uuid) {
    Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
    Location eyeLocation = player.getEyeLocation();
    World world = eyeLocation.getWorld();
    Optional<Player> min = Bukkit.getOnlinePlayers()
        .stream().map(p -> (Player) p).filter(p -> !p.equals(player)
            && !CooldownItem.hasCooldown(p.getUniqueId(), "HideAmpouleItem")
            && p.getWorld().equals(world))
        .min(Comparator.comparingDouble(o -> eyeLocation.distanceSquared(o.getEyeLocation())));
    int cooldown = getIntCooldown(uuid);
    if (min.isEmpty()) {
      return Optional.of("<red>残り" + cooldown + "秒");
    }

    Vector direction = min.get().getLocation().toVector().subtract(eyeLocation.toVector()).setY(0);
    Vector playerDirection = eyeLocation.getDirection().setY(0);
    double angle = direction.angle(playerDirection);
    double dotProduct = new Vector(0, 1, 0).dot(direction.crossProduct(playerDirection));
    angle = angle * Math.signum(dotProduct);

    double degree = Math.toDegrees(angle);

    /*
      0 == |||||| | |||||| (6,1,6)
      90 == |||||||||||| | (12,1)
      -90 == | |||||||||||| (1,12)

      ->

      -82.5 => x > -97.5 == 1
      -67.5 => x > -82.5 == 2
      -52.5 => x > -67.5 == 3
      -37.5 => x > -52.5 == 4
      -22.5 => x > -37.5 == 5
      -7.5 => x > -22.5 == 6
      7.5 => x > -7.5 == 7
      22.5 => x > 7.5 == 8
      37.5 => x > 22.5 == 9
      90 == 13
      -90 == 1
     */

    int v = (int) ((degree + 112) / 15);

    String builder = IntStream.range(1, 14)
        .mapToObj(i -> v == i ? "<red>" + stick + "<white>" : i == 7 ? "|<blue>|<white>|" : stick)
        .collect(Collectors.joining("", "<white>", ""));

    return Optional.of(" ".repeat(9 + String.valueOf(cooldown).length())
        + builder + " <red>残り" + cooldown + "秒");
  }

  @Override
  public @NotNull String getIdentifier() {
    return "OfroSonerItem";
  }
}
