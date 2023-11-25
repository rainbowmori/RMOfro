package github.rainbowmori.ofro.config;

import github.teamofro.ofrolib.OfroLib;
import github.teamofro.ofrolib.api.ConfigAPI;
import github.teamofro.ofrolib.utils.FormatterUtil;
import github.teamofro.ofrolib.utils.Util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class DestinationConfig extends ConfigAPI {

	// TODO: ここで {arg-1} {arg-2} とかで cmd:say {hello} {world}
	// とかで対応させるとか面白そうちゃう？
	private final Map<String, List<String>> commandMap = new HashMap<>();

	public DestinationConfig() {
		super(OfroLib.getPlugin(), "CommandDestination.yml");
		ConfigurationSection commandsSection = data.getConfigurationSection("commands");
		if (commandsSection == null) {
			return;
		}
		for (String commands : commandsSection.getKeys(false)) {
			commandMap.put(commands, commandsSection.getStringList(commands));
		}
	}

	public void runCommand(Player player, String commandName) {
		List<String> commandList = commandMap.get(commandName);
		if (commandList == null || commandList.isEmpty()) {
			return;
		}
		commandList.forEach(s -> {
			String[] split = s.split(":");
			if (split.length == 0) {
				return;
			}
			if (split.length == 1) {
				player.performCommand(parse(player, s));
				return;
			}
			switch (split[0]) {
				case "console" -> Util.consoleCommand(parse(player, split[1]));
				case "op" -> Util.executeCommand(player, parse(player, split[1]));
				default -> OfroLib.getPlugin().prefixUtil.logWarn(
						"MVDestinationの" + commandName + "のcommandが" + split[0] + "として識別子が入力されています");
			}
		});
	}

	private String parse(Player player, String command) {
		return FormatterUtil.format(command,
				Map.of("player", player.getName(), "world", player.getWorld().getName()));
	}
}
