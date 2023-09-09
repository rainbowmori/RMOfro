package github.rainbowmori.ofro.object.customblock.model;

import github.rainbowmori.ofro.command.ofro.BankCommand;
import github.rainbowmori.ofro.object.customitem.ATMItem;
import github.rainbowmori.rainbowapi.object.customblock.CustomModelBlock;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ATMBlock extends CustomModelBlock {

  public ATMBlock(Location location) {
    super(location);
  }

  @Override
  public ItemStack getItem() {
    return new ATMItem().getItem();
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    BankCommand.openBank(e.getPlayer());
  }

  @Override
  public String getIdentifier() {
    return getClass().getSimpleName();
  }
}
