package github.rainbowmori.ofro.object.customitem;

import github.rainbowmori.ofro.object.customblock.model.ResourceVaultBlock;
import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ResourceVaultItem extends CustomItem {

  public ResourceVaultItem() {
    this(new ItemBuilder(Material.STONE_HOE).name("<gray>資源保管庫").customModelData(3).build());
  }

  public ResourceVaultItem(ItemStack item) {
    super(item);
  }

  @Override
  public @NotNull String getIdentifier() {
    return getClass().getSimpleName();
  }

  @Override
  public void leftClick(PlayerInteractEvent e) {

  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    if (!clickedBlock(e)) {
      return;
    }
    relativeLocation(e, ResourceVaultBlock::new);
    itemUse();
  }

  @Override
  public @NotNull Optional<String> getActionBarMessage(Player player) {
    return Optional.empty();
  }
}
