package github.rainbowmori.ofro.object.customblock.model;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.object.itemvalue.RemoteMaterialTransmitter;
import github.rainbowmori.ofro.object.customitem.RemoteMaterialTransmitterItem;
import github.rainbowmori.rainbowapi.object.customblock.CustomModelBlock;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RemoteMaterialTransmitterBlock extends CustomModelBlock {

  private final Set<Inventory> inventories = new HashSet<>();

  public RemoteMaterialTransmitterBlock(Location location) {
    super(location);
  }

  @Override
  public ItemStack getItem() {
    return new RemoteMaterialTransmitterItem().getItem();
  }

  @Override
  public void clearData(Location location) {
    super.clearData(location);
    inventories.forEach(Inventory::close);
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    Inventory inventory = new RemoteMaterialTransmitter(Ofro.getPlugin()).getInventory();
    inventories.add(inventory);
    e.getPlayer().openInventory(inventory);
  }

  @Override
  public String getIdentifier() {
    return getClass().getSimpleName();
  }
}
