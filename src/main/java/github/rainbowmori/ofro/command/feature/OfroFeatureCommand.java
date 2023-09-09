package github.rainbowmori.ofro.command.feature;

import github.rainbowmori.rainbowapi.dependencies.commandapi.CommandAPICommand;

public class OfroFeatureCommand extends CommandAPICommand {

  public OfroFeatureCommand() {
    super("ofrofeature");
    withSubcommand(new BankCommand());
    withSubcommand(new ItemValueShopCommand());
  }
}
