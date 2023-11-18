package github.rainbowmori.ofro.command;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.multiverseinventories.WorldGroup;
import com.onarandombox.multiverseinventories.profile.WorldGroupManager;
import com.onarandombox.multiverseinventories.share.Sharables;
import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.ofro.object.config.rtp.RTP;
import github.rainbowmori.ofro.object.savage.SavageWorld;
import github.rainbowmori.rainbowapi.api.FileAPI;
import github.rainbowmori.rainbowapi.dependencies.commandapi.CommandAPI;
import github.rainbowmori.rainbowapi.dependencies.commandapi.CommandPermission;
import github.rainbowmori.rainbowapi.dependencies.commandapi.CommandTree;
import github.rainbowmori.rainbowapi.dependencies.commandapi.OriginalArguments;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.Argument;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.ArgumentSuggestions;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.BooleanArgument;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.LiteralArgument;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.PlayerArgument;
import github.rainbowmori.rainbowapi.util.Util;
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

public class OfroCommand extends CommandTree {

  private static final Map<String, String> worldResetDay = new HashMap<>() {
    {
      put("savage001", "月曜日");
      put("savage002", "水曜日");
    }
  };

  public static Argument<Player> getPlayer(String nodeName) {
    return new PlayerArgument(nodeName).replaceSuggestions(
        ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers()
            .stream()
            .map(Player::getName)
            .toArray(String[]::new)));
  }

  public OfroCommand() {
    super("ofro");
    withPermission(CommandPermission.OP);
    then(new LiteralArgument("reloadconfig")
        .executes((commandSender, objects) -> {
          Ofro.getConfigService().save();
          Ofro.initConfigService();
          OfroPrefix.OFRO.send(commandSender,
              "<green>すべてのconfigをreloadしました");
        }));
    then(new LiteralArgument("rtp")
        .executesPlayer((player, objects) -> {
          new RTP(player, player).rtp(player.getWorld());
        })
        .then(
            getPlayer("player")
                .executes((commandSender, objects) -> {
                  Player player = (Player) objects.get(0);
                  new RTP(player, commandSender).rtp(player.getWorld());
                })
                .then(OriginalArguments.worlds("worlds")
                    .then(new BooleanArgument("bedInclude")
                        .executes((commandSender, objects) -> {
                          new RTP(((Player) objects.get(0)),
                              commandSender)
                              .rtp((World) objects.get(1),
                                  ((boolean) objects.get(2)));
                        }))
                    .executes((commandSender, objects) -> {
                      new RTP(((Player) objects.get(0)),
                          commandSender)
                          .rtp((World) objects.get(1));
                    }))));
    then(new LiteralArgument("savage").then(
        OfroArguments.getSavageWorld("savageWorld")
            .then(
                new LiteralArgument("bed")
                    .then(new LiteralArgument("show").executes((commandSender,
                        objects) -> {
                      World savageWorld = (World) objects.get(0);
                      if (!savageWorld.isBedWorks()) {
                        throw CommandAPI.failWithString(
                            Util.cc("&c" + savageWorld.getName() +
                                "ではベッドを使えません"));
                      }
                      OfroPrefix.TP.send(commandSender,
                          "<yellow>=== <red>" +
                              savageWorld.getName() +
                              " <yellow>===");
                      SavageWorld.getSavageWorld(savageWorld).savagePlayer.forEach(
                          (uuid, savagePlayer) -> savagePlayer.getOptionalLocation().ifPresent(
                              location -> {
                                OfroPrefix.TP.send(
                                    commandSender,
                                    "<blue>%s <reset>: x = %s , y = %s ,z = %s"
                                        .formatted(
                                            Bukkit
                                                .getOfflinePlayer(uuid)
                                                .getName(),
                                            location.getX(),
                                            location.getY(),
                                            location.getZ()));
                              }));
                    }))
                    .then(new LiteralArgument("clear").executes((sender,
                        args) -> {
                      World savageWorld = (World) args.get(0);
                      if (!savageWorld.isBedWorks()) {
                        OfroPrefix.TP.send(sender,
                            "<red>" + savageWorld.getName() +
                                "ではベッドを使えません");
                        return;
                      }
                      OfroPrefix.TP.send(
                          sender,
                          "<blue>" + savageWorld.getName() +
                              " <green>ワールドのbed locationを削除しました");
                      SavageWorld.getSavageWorld(savageWorld).savagePlayer.forEach(
                          (uuid,
                              savagePlayer) -> savagePlayer.setBedLoc(null));
                    })))
            .then(new LiteralArgument("restart").executes(
                (commandSender, objects) -> {
                  savageWorldReset(((World) objects.get(0)));
                }))));
  }

  private void savageWorldReset(World world) {
    String worldName = world.getName();
    if (!SavageWorld.isSavageWorld(world)) {
      Ofro.getPlugin().getPrefixUtil().logWarn(
          worldName +
              "はsavageWorldではありません : " + SavageWorld.savageName);
      return;
    }
    if (!world.isNatural()) {
      Ofro.getPlugin().getPrefixUtil().logWarn(worldName +
          "はnaturalではありません");
      return;
    }
    OfroPrefix.BC.Cast(
        "<dark_red><bold>" + worldName +
            "のワールドがリセットされています。ラグにご注意ください！");
    SavageWorld.getSavageWorld(world).remove();
    WorldGroupManager groupManager = Ofro.getInventories().getGroupManager();
    Optional.ofNullable(groupManager.getGroup(worldName))
        .ifPresent(groupManager::removeGroup);
    Ofro.getInventories().reloadConfig();

    WorldGroup worldGroup = groupManager.newEmptyGroup(worldName);
    String nether = worldName + "_nether";
    create(worldGroup, worldName, nether, Environment.NORMAL);
    create(worldGroup, nether, worldName, Environment.NETHER);
    Ofro.getInventories().reloadConfig();
    String day = worldResetDay.get(worldName);
    OfroPrefix.BC.Cast("<green><bold>" + worldName +
        "のワールドがリセットされました。" +
        (day == null ? ""
            : "次回のワールドリセットは<reset>来週の" +
                day + "<reset><green><bold>です。"));
    worldGroup.getShares().addAll(Sharables.allOf());
    groupManager.updateGroup(worldGroup);
    Ofro.getInventories().reloadConfig();
  }

  private void create(WorldGroup group, String worldName, String portal,
      Environment environment) {
    MVWorldManager mvWorldManager = Ofro.getCore().getMVWorldManager();
    if (Bukkit.getWorld(worldName) != null) {
      mvWorldManager.deleteWorld(worldName);
      mvWorldManager.removeWorldFromConfig(worldName);
      FileAPI.folderClear(new File(Ofro.getInventories().getDataFolder() +
          "/worlds/" + worldName));
      FileAPI.folderClear(new File(Ofro.getInventories().getDataFolder() +
          "/groups/" + worldName));
    }
    mvWorldManager.addWorld(worldName, environment, null, WorldType.NORMAL,
        true, null);
    Ofro.getNetherPortals().addWorldLink(worldName, portal, PortalType.NETHER);
    group.addWorld(worldName);
  }
}
