package github.rainbowmori.ofro.object.itemvalue;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.rainbowapi.dependencies.ui.button.MenuButton;
import github.rainbowmori.rainbowapi.dependencies.ui.button.RedirectItemButton;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class ShopCategory {

  private final MenuButton<?> button;

  public ShopCategory(ItemStack itemStack, List<MaterialInfo> materials) {
    this.button = new RedirectItemButton<>(itemStack, () -> new ItemValueShopPage(Ofro.getPlugin(), itemStack.displayName(), materials).getInventory());
  }

  public ShopCategory(MenuButton<?> button) {
    this.button = button;
  }

  public MenuButton<?> getButton() {
    return button;
  }
}
