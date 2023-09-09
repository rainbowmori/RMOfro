package github.rainbowmori.ofro.command;

import github.rainbowmori.ofro.object.savage.SavageWorld;
import github.rainbowmori.rainbowapi.dependencies.commandapi.OriginalArguments;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.Argument;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.ArgumentSuggestions;
import github.rainbowmori.rainbowapi.dependencies.commandapi.arguments.CustomArgument;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class OfroArguments {
    public static Argument<?> getSavageWorld(String nodeName) {
        return new CustomArgument<>(OriginalArguments.worlds(nodeName), info -> {
            World world = info.currentInput();
            if (SavageWorld.isSavageWorld(world)) {
                return world;
            }
            throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Savage world ではありません : ").appendArgInput());
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getWorlds().stream().map(World::getName)
                .filter(s -> s.startsWith(SavageWorld.savageName)).toArray(String[]::new)));
    }
}
