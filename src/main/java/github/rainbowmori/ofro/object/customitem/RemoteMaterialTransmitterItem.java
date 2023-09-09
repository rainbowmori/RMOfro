package github.rainbowmori.ofro.object.customitem;

import github.rainbowmori.ofro.object.customblock.model.RemoteMaterialTransmitterBlock;
import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RemoteMaterialTransmitterItem extends CustomItem {

  public RemoteMaterialTransmitterItem() {
    this(new ItemBuilder(Material.STONE_HOE).name("<gold><bold>遠隔物資送信機")
            .addLore("<yellow>売却対象アイテムをショップに送信することができる。")
            .addLore("<red>右クリックしたブロックの<bold><u>上に<reset><red>設置されるので注意！")
            .customModelData(2).build());
  }

  public RemoteMaterialTransmitterItem(ItemStack item) {
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
    relativeLocation(e, RemoteMaterialTransmitterBlock::new);
    itemUse();
  }

  @Override
  public @NotNull Optional<String> getActionBarMessage(Player player) {
    return Optional.empty();
  }
}
