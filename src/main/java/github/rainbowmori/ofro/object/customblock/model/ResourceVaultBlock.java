package github.rainbowmori.ofro.object.customblock.model;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.object.itemvalue.ResourceVault;
import github.rainbowmori.ofro.object.customitem.ResourceVaultItem;
import github.rainbowmori.rainbowapi.object.customblock.CustomModelBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ResourceVaultBlock extends CustomModelBlock {

  public ResourceVaultBlock(Location location) {
    super(location);
  }

  @Override
  public ItemStack getItem() {
    return new ResourceVaultItem().getItem();
  }

  @Override
  public void leftClick(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    if (isCreative(player)) {
      super.leftClick(e);
    }
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    e.getPlayer().openInventory(new ResourceVault(Ofro.getPlugin()).getInventory());
  }

  @Override
  public String getIdentifier() {
    return getClass().getSimpleName();
  }
}
