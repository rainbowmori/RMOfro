package github.rainbowmori.ofro.object.itemvalue;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.rainbowapi.dependencies.ui.menu.MenuHolder;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class RemoteMaterialTransmitter extends MenuHolder<Ofro> {

  public RemoteMaterialTransmitter(Ofro plugin) {
    super(plugin, 54, "<gold><bold>売却アイテムをここに入れて下さい");
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    event.setCancelled(false);
  }

  @Override
  public void onClose(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();
    UUID uniqueId = player.getUniqueId();
    int price = 0;
    boolean isNotItemValue = false;
    for (ItemStack item : event.getInventory().getStorageContents()) {
      Material type;
      if (item == null || (type = item.getType()) == Material.AIR) {
        continue;
      }
      MaterialInfo info;
      if (!MaterialShop.containMaterial(type) || !(info = MaterialShop.getInfo(type)).canSell()) {
        isNotItemValue = true;
        player.getInventory().addItem(item);
        continue;
      }
      price += (info.sellPrice() * item.getAmount());
    }
    if (isNotItemValue) {
      OfroPrefix.BANK.send(player, "<red>売却できるアイテム以外が入っていたのでそれは返却しました");
    }
    if (price != 0) {
      Ofro.getConfigService().PLAYERDATA.addRMTSell(uniqueId, price);
      OfroPrefix.BANK.send(player, "<green>売却対象アイテムはshopの資源保管庫にて換金されました！");
      OfroPrefix.BANK.send(player, "<yellow>お金を引き出すには、shopの資源保管庫より引き出してください。");
      player.playSound(player,Sound.ENTITY_PLAYER_LEVELUP,1,1);
    }
  }
}
