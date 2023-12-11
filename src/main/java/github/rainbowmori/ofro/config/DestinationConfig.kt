package github.rainbowmori.ofro.config

import github.teamofro.ofrolib.OfroLib
import github.teamofro.ofrolib.api.ConfigAPI
import github.teamofro.ofrolib.utils.FormatterUtil
import github.teamofro.ofrolib.utils.Util
import org.bukkit.entity.Player

class DestinationConfig : ConfigAPI(OfroLib.getPlugin(), "CommandDestination.yml") {
  // TODO: ここで {arg-1} {arg-2} とかで cmd:say {hello} {world}
  // とかで対応させるとか面白そうちゃう？
  private val commandMap: MutableMap<String, List<String>> = HashMap()

  init {
    val commandsSection = data.getConfigurationSection("commands")
    if (commandsSection != null) {
      for (commands in commandsSection.getKeys(false)) {
        commandMap[commands] = commandsSection.getStringList(commands)
      }
    }
  }

  fun runCommand(
      player: Player,
      commandName: String,
  ) {
    val commandList = commandMap[commandName]
    if (commandList.isNullOrEmpty()) {
      return
    }
    commandList.forEach forEach@{ s: String ->
      val split = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      if (split.isEmpty()) {
        return@forEach
      }
      if (split.size == 1) {
        player.performCommand(parse(player, s))
        return@forEach
      }
      when (split[0]) {
        "console" -> Util.consoleCommand(parse(player, split[1]))
        "op" -> Util.executeCommand(player, parse(player, split[1]))
        else -> OfroLib.getPlugin().prefixUtil.logWarn(
                "MVDestinationの" + commandName + "のcommandが" + split[0] + "として識別子が入力されています",
            )
      }
    }
  }

  private fun parse(
      player: Player,
      command: String,
  ): String {
    return FormatterUtil.format(
        command,
        mapOf("player" to player.name, "world" to player.world.name),
    )
  }
}
