package github.rainbowmori.ofro.object.config.rtp;

import com.onarandombox.multiverseinventories.utils.paperlib.PaperLib;
import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.ofro.object.config.rtp.world.RTPWorld;
import github.rainbowmori.ofro.object.savage.SavageWorld;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RTP {

  private static final Set<UUID> rtping = new HashSet<>();
  private final RTPConfig config;
  private final Random random = ThreadLocalRandom.current();

  private final Player player;
  private final CommandSender sender;
  private final Set<Material> bat_blocks = new HashSet<>() {{
    add(Material.LAVA);
    add(Material.WATER);
    add(Material.FIRE);
    add(Material.CACTUS);
  }};

  private final Predicate<Material> predicate = material -> !bat_blocks.contains(material);
  public int maxAttempts;
  public int minRadius;
  public int maxRadius;
  public int maxY;
  public int minY;
  private World toWorld;
  private int CenterX;
  private int CenterZ;
  private int attempts;

  public RTP(Player player, CommandSender sender) {
    this.player = player;
    this.sender = sender;
    this.config = Ofro.getConfigService().RTP;
  }

  public void rtp(World world) {
    if (rtping.contains(player.getUniqueId())) {
      config.AlreadyMessage.send(player, sender);
      return;
    }
    rtping.add(player.getUniqueId());
    config.StartMessage.send(player, sender);
    toWorld = world;
    RTPWorld rtpWorld = config.getRTPWorld(toWorld.getName());
    if (rtpWorld.getCenterOfWorldSpawn()) {
      CenterX = toWorld.getSpawnLocation().getBlockX();
      CenterZ = toWorld.getSpawnLocation().getBlockZ();
    } else {
      CenterX = rtpWorld.getCenterX();
      CenterZ = rtpWorld.getCenterZ();
    }
    maxAttempts = config.maxAttempts;
    minRadius = rtpWorld.getMinRadius();
    maxRadius = rtpWorld.getMaxRadius();
    maxY = rtpWorld.getMaxY();
    minY = rtpWorld.getMinY();
    teleport();
  }

  public void rtp(World world, boolean bedInclude) {
    if (bedInclude && SavageWorld.isSavageWorld(world) && world.isBedWorks()
        && SavageWorld.getSavageWorld(world)
        .getSavagePlayer(player.getUniqueId()).teleportBed(player)) {
      return;
    }
    rtp(world);
  }

  private void failed() {
    config.FailedMessage.send(player, sender);
    rtping.remove(player.getUniqueId());
  }

  private void success(final Location finalLoc) {
    player.playSound(player,Sound.ENTITY_GENERIC_EXPLODE,1,1);
    player.spawnParticle(Particle.EXPLOSION_NORMAL, finalLoc, 1);
    player.spawnParticle(Particle.CRIT, finalLoc, 1);
    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
    config.SuccessMessage.sendLocation(player, sender, finalLoc);
    rtping.remove(player.getUniqueId());
  }

  private void teleport() {
    Bukkit.getScheduler().runTaskTimer(Ofro.getPlugin(), (bukkitTask) -> {
      if (attempts > maxAttempts) {
        failed();
        bukkitTask.cancel();
      } else {
        if (attempts % 10 == 0) {
          player.playSound(player,Sound.ENTITY_TNT_PRIMED,1,1);
        }
        attempts++;
        Location randomLocation = getRandom();
        if (isLocationSafe(randomLocation)) {
          bukkitTask.cancel();
          randomLocation.add(0.5, 1, 0.5);
          PaperLib.teleportAsync(player, randomLocation, TeleportCause.PLUGIN)
              .thenAccept(aBoolean -> {
                if (aBoolean) {
                  Bukkit.getScheduler().runTask(Ofro.getPlugin(), () -> success(randomLocation));
                } else {
                  Bukkit.getScheduler().runTask(Ofro.getPlugin(),
                      () -> OfroPrefix.TP.send(player, "<red>RTPでTP先にテレポートできませんでした!"));
                }
              });
        }
      }
    }, 0L, 1L);
  }

  private Location getRandom() {
    double angle = random.nextDouble(361) * Math.PI / 180;
    double radius = random.nextDouble(minRadius, maxRadius);
    int x = (int) (radius * Math.cos(angle)) + CenterX;
    int z = (int) (radius * Math.sin(angle)) + CenterZ;
    return toWorld.getHighestBlockAt(x, z).getLocation();
  }

  private boolean isLocationSafe(Location location) {
    if (location.getY() > maxY || minY > location.getY()) {
      return false;
    }
    Block below = location.getBlock();
    return predicate.test(below.getType()) && below.getType().isSolid();
  }
}
