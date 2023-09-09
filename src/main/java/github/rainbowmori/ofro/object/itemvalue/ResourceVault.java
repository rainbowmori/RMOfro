package github.rainbowmori.ofro.object.itemvalue;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.ofro.object.config.PlayerDataConfig;
import github.rainbowmori.rainbowapi.dependencies.ui.button.ItemButton;
import github.rainbowmori.rainbowapi.dependencies.ui.menu.MenuHolder;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class ResourceVault extends MenuHolder<Ofro> {

  public ResourceVault(Ofro plugin) {
    super(plugin, InventoryType.HOPPER, "<dark_green><bold>売却結果");
  }

  @Override
  public void onOpen(InventoryOpenEvent event) {
    Player player = (Player) event.getPlayer();
    UUID uuid = player.getUniqueId();
    PlayerDataConfig playerdata = Ofro.getConfigService().PLAYERDATA;
    ItemButton<MenuHolder<?>> button = new ItemButton<>(
        new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("<green><bold>受け取る").build());
    setButton(1, button);
    setButton(3, button);
    setButton(2,
        new ItemButton<>(playerdata.hasRMTSell(uuid) ? new ItemBuilder(Material.PAPER).name(
            "売却結果: <yellow><bold><u>%s円".formatted(
                playerdata.getRMTSell(uuid))).build()
            : new ItemBuilder(Material.PAPER).name("<red><bold>アイテムが保管されていません！").build()) {
          @Override
          public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
            getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
              event.getView().close();
              Player whoClicked = (Player) event.getWhoClicked();
              OfroPrefix.BANK.send(whoClicked,
                  "<yellow><bold><u>%s円<reset><green>受け取りました。".formatted(
                      playerdata.getRMTSell(uuid)));
              playerdata.resetRMTSell(uuid);
              Ofro.getEconomy().depositPlayer(whoClicked, playerdata.getRMTSell(uuid));
              player.playSound(player,Sound.ENTITY_PLAYER_LEVELUP,1,1);
            });
          }
        });

  }
}
