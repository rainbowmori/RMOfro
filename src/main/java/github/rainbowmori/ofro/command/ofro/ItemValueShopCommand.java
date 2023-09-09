package github.rainbowmori.ofro.command.feature;

import github.rainbowmori.ofro.object.itemvalue.MaterialShop;
import github.rainbowmori.rainbowapi.dependencies.commandapi.CommandAPICommand;
import github.rainbowmori.rainbowapi.dependencies.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

public class ItemValueShopCommand extends CommandAPICommand {

  public ItemValueShopCommand() {
    super("itemvalueshop");
    executesPlayer(ItemValueShopCommand::run);
  }



  private static void run(Player sender, CommandArguments args) {
    MaterialShop.openShop(sender);
  }
}
