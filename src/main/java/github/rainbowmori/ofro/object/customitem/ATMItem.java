package github.rainbowmori.ofro.object.customitem;

import github.rainbowmori.ofro.object.customblock.model.ATMBlock;
import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ATMItem extends CustomItem {

  public ATMItem() {
    this(new ItemBuilder(Material.STONE_HOE).customModelData(1).name("<white>ATM").build());
  }

  public ATMItem(ItemStack item) {
    super(item);
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    if (!clickedBlock(e)) {
      return;
    }
    relativeLocation(e, ATMBlock::new);
    itemUse();
  }

  @Override
  public @NotNull String getIdentifier() {
    return getClass().getSimpleName();
  }

  @Override
  public @NotNull Optional<String> getActionBarMessage(Player player) {
    return Optional.empty();
  }
}
