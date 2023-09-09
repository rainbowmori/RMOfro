package github.rainbowmori.ofro.command.ofro;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.multiverseinventories.WorldGroup;
import com.onarandombox.multiverseinventories.profile.WorldGroupManager;
import com.onarandombox.multiverseinventories.share.Sharables;
import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.command.OfroArguments;
import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.ofro.object.config.rtp.RTP;
import github.rainbowmori.ofro.object.savage.SavageWorld;
import github.rainbowmori.rainbowapi.api.FileAPI;
import github.rainbowmori.rainbowapi.object.commandapi.CommandAPI;
import github.rainbowmori.rainbowapi.object.commandapi.CommandAPICommand;
import github.rainbowmori.rainbowapi.object.commandapi.CommandPermission;
import github.rainbowmori.rainbowapi.object.commandapi.OriginalArguments;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.BooleanArgument;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

public class OfroCommand extends CommandAPICommand {

  private static final Map<String, String> worldResetDay = new HashMap<>() {{
    put("savage001", "月曜日");
    put("savage002", "水曜日");

  }};

  public OfroCommand() {
    super("ofro");
    withPermission(CommandPermission.OP);
    withSubcommand(new BankCommand());
    withSubcommand(new ItemValueShopCommand());
    withSubcommand(
        new CommandAPICommand("reloadconfig")
            .executes((commandSender, args) -> {
              Ofro.getConfigService().save();
              Ofro.initConfigService();
              OfroPrefix.OFRO.send(commandSender, "<green>すべてのconfigをreloadしました");
            })
    );
    withSubcommands(
        new CommandAPICommand("rtp")
            .executesPlayer((sender, args) -> {
              new RTP(sender, sender).rtp(sender.getWorld());
            }),
        new CommandAPICommand("rtp")
            .withArguments(OriginalArguments.onlinePlayer("player"))
            .withOptionalArguments(OriginalArguments.worlds("world"))
            .withOptionalArguments(new BooleanArgument("bed"))
            .executes((sender, args) -> {
              Player player = (Player) args.get("player");
              World world = (World) args.getOptional("world").orElse(player.getWorld());
              boolean bedInclude = (boolean) args.getOptional("bed").orElse(false);
              new RTP(player, sender).rtp(world, bedInclude);
            })
    );
    withSubcommand(
        new CommandAPICommand("savage")
            .withArguments(OfroArguments.getSavageWorld("savageWorld"))
            .withSubcommand(
                new CommandAPICommand("bed")
                    .withSubcommand(
                        new CommandAPICommand("show")
                            .executes((sender, args) -> {
                              World savageWorld = (World) args.get("savageWorld");
                              ;
                              assert savageWorld != null;
                              if (!savageWorld.isBedWorks()) {
                                throw CommandAPI.failWithString("このワールドはベッドが使用できません");
                              }
                              OfroPrefix.TP.send(sender,
                                  "<yellow>=== <red>" + savageWorld.getName() + " <yellow>===");
                              SavageWorld.getSavageWorld(savageWorld).savagePlayer.forEach(
                                  (uuid, savagePlayer) -> savagePlayer.getOptionalLocation()
                                      .ifPresent(location -> {
                                        OfroPrefix.TP.send(sender,
                                            "<blue>%s <reset>: x = %s , y = %s ,z = %s".
                                                formatted(Bukkit.getOfflinePlayer(uuid).getName(),
                                                    location.getX(),
                                                    location.getY(), location.getZ()));
                                      }));
                            })
                    )
                    .withSubcommand(
                        new CommandAPICommand("clear")
                            .executes((sender, args) -> {
                              World savageWorld = (World) args.get("savageWorld");
                              if (!savageWorld.isBedWorks()) {
                                OfroPrefix.TP.send(sender,
                                    "<red>" + savageWorld.getName() + "ではベッドを使えません");
                                return;
                              }
                              OfroPrefix.TP.send(sender,
                                  "<blue>" + savageWorld.getName()
                                      + " <green>ワールドのbed locationを削除しました");
                              SavageWorld.getSavageWorld(savageWorld).savagePlayer
                                  .forEach((uuid, savagePlayer) -> savagePlayer.setBedLoc(null));
                            })
                    )
            )
            .withSubcommand(
                new CommandAPICommand("restart")
                    .executes((sender, args) -> {
                      savageWorldReset(((World) args.get("savageWorld")));
                    })
            )
    );
  }

  private void savageWorldReset(World world) {
    String worldName = world.getName();
    if (!SavageWorld.isSavageWorld(world)) {
      Ofro.getPlugin().getPrefixUtil().logWarn(
          worldName + "はsavageWorldではありません : " + SavageWorld.savageName);
      return;
    }
    if (!world.isNatural()) {
      Ofro.getPlugin().getPrefixUtil().logWarn(worldName + "はnaturalではありません");
      return;
    }
    OfroPrefix.BC.Cast("<dark_red><bold>" + worldName + "のワールドがリセットされています。ラグにご注意ください！");
    SavageWorld.getSavageWorld(world).remove();
    WorldGroupManager groupManager = Ofro.getInventories().getGroupManager();
    Optional.ofNullable(groupManager.getGroup(worldName)).ifPresent(groupManager::removeGroup);
    Ofro.getInventories().reloadConfig();

    WorldGroup worldGroup = groupManager.newEmptyGroup(worldName);
    String nether = worldName + "_nether";
    create(worldGroup, worldName, nether, Environment.NORMAL);
    create(worldGroup, nether, worldName, Environment.NETHER);
    Ofro.getInventories().reloadConfig();
    String day = worldResetDay.get(worldName);
    OfroPrefix.BC.Cast("<green><bold>" + worldName + "のワールドがリセットされました。" +
        (day == null ? "" : "次回のワールドリセットは<reset>来週の" + day + "<reset><green><bold>です。"));
    worldGroup.getShares().addAll(Sharables.allOf());
    groupManager.updateGroup(worldGroup);
    Ofro.getInventories().reloadConfig();
  }

  private void create(WorldGroup group, String worldName, String portal, Environment environment) {
    MVWorldManager mvWorldManager = Ofro.getCore().getMVWorldManager();
    if (Bukkit.getWorld(worldName) != null) {
      mvWorldManager.deleteWorld(worldName);
      mvWorldManager.removeWorldFromConfig(worldName);
      FileAPI.folderClear(new File(Ofro.getInventories().getDataFolder() + "/worlds/" + worldName));
      FileAPI.folderClear(new File(Ofro.getInventories().getDataFolder() + "/groups/" + worldName));
    }
    mvWorldManager.addWorld(worldName, environment, null, WorldType.NORMAL, true, null);
    Ofro.getNetherPortals().addWorldLink(worldName, portal, PortalType.NETHER);
    group.addWorld(worldName);
  }
}
