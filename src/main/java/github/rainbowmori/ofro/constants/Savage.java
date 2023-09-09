package github.rainbowmori.ofro.constants;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.rainbowapi.util.FormatterUtil;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Savage {

  public static void dropMoney(OfflinePlayer player, Location location) {
    Economy economy = Ofro.getEconomy();
    double balance = economy.getBalance(player);
    ThreadLocalRandom current = ThreadLocalRandom.current();
    ItemStack money = null;
    int moneyInteger = 0;
    if (balance >= 100000) {
      if (current.nextBoolean()) {
        moneyInteger = 10000;
        money = Items.get10000Yen();
      }
    } else if (balance >= 50000) {
      if (current.nextInt(3) == 0) {
        moneyInteger = 1000;
        money = Items.get1000Yen();
      }
    } else if (balance >= 10000) {
      if (current.nextInt(4) == 0) {
        moneyInteger = 100;
        money = Items.get100Yen();
      }
    }
    World world = location.getWorld();
    if (money != null) {
      Util.cast(FormatterUtil.format(Ofro.getConfigService().MESSAGE.SAVAGE_DROP_MONEY, Map.of("player", player.getName(), "money", moneyInteger)));
      economy.withdrawPlayer(player, moneyInteger);
      world.dropItemNaturally(location, money);
    }
  }
}
