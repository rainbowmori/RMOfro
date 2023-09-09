package github.rainbowmori.ofro.object.itemvalue;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.rainbowapi.dependencies.ui.menu.MenuHolder;
import java.util.List;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public final class ItemValueShopMenu extends MenuHolder<Ofro> {

  final int rewardStartIndex, rewardEndIndex;
  final List<MaterialInfo> values;
  public ItemValueShopPage page;

  public ItemValueShopMenu(Ofro plugin, List<MaterialInfo> values, int rewardStartIndex,
      int rewardEndIndex) {
    super(plugin, 45, "<red>SHOP");
    this.rewardStartIndex = rewardStartIndex;
    this.rewardEndIndex = rewardEndIndex;
    this.values = values;
  }

  @Override
  public void onOpen(InventoryOpenEvent event) {
    for (int slot = 0; slot < getInventory().getSize() && rewardStartIndex + slot < rewardEndIndex; slot++) {
      MaterialInfo value = values.get(rewardStartIndex + slot);
      if (value == null) {
        continue;
      }
      setButton(slot, value.createButton(page.getInventory()));
    }
  }

  @Override
  public void onClose(InventoryCloseEvent event) {
    clearButtons();
  }
}
