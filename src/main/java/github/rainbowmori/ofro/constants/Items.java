package github.rainbowmori.ofro.constants;

import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Ofro鯖での不変的なアイテムたちです
 */
public class Items {

  public static ItemStack get1Yen() {
    return get1Yen(1);
  }

  public static ItemStack get1Yen(int amount) {
    return oneYen.amount(amount).build();
  }

  public static ItemStack get10Yen() {
    return get10Yen(1);
  }

  public static ItemStack get10Yen(int amount) {
    return tenYen.amount(amount).build();
  }

  public static ItemStack get100Yen() {
    return get100Yen(1);
  }

  public static ItemStack get100Yen(int amount) {
    return hundredYen.amount(amount).build();
  }

  public static ItemStack get1000Yen() {
    return get1000Yen(1);
  }

  public static ItemStack get1000Yen(int amount) {
    return thousandYen.amount(amount).build();
  }

  public static ItemStack get10000Yen() {
    return get10000Yen(1);
  }

  public static ItemStack get10000Yen(int amount) {
    return tenThousandYen.amount(amount).build();
  }

  private static final ItemBuilder oneYen = new ItemBuilder(Material.IRON_NUGGET)
      .name(Util.component("&8&l&ko&r&7&l御風呂コイン - アルミ&8&l&ko&r"))
      .lore(Util.component("&d1円の価値があるコイン。"),
          Util.component("&dサーバー内で1円として使用できる。"))
      .customModelData(1).glow();

  private static final ItemBuilder tenYen = new ItemBuilder(Material.IRON_NUGGET)
      .name(Util.component("&7&l&ko&r&6&l御風呂コイン - ブロンズ&7&l&ko&r"))
      .lore(Util.component("&d10円の価値があるコイン。"),
          Util.component("&dサーバー内で10円として使用できる。"))
      .customModelData(2).glow();

  private static final ItemBuilder hundredYen = new ItemBuilder(Material.IRON_NUGGET)
      .name(Util.component("&6&l&ko&r&e&l御風呂コイン - ゴールド&6&l&ko&r"))
      .lore(Util.component("&d100円の価値があるコイン。"),
          Util.component("&dサーバー内で100円として使用できる。"))
      .customModelData(3).glow();

  private static final ItemBuilder thousandYen = new ItemBuilder(Material.IRON_NUGGET)
      .name(Util.component("&4&l&ko&r&c&l御風呂コイン - レッドストーン&4&l&ko&r"))
      .lore(Util.component("&d1000円の価値があるコイン。"),
          Util.component("&dサーバー内で1000円として使用できる。"))
      .customModelData(4).glow();

  private static final ItemBuilder tenThousandYen = new ItemBuilder(
      Material.IRON_NUGGET)
      .name(Util.component("&1&l&ko&r&b&l御風呂コイン - ダイヤモンド&1&l&ko&r"))
      .lore(Util.component("&d10000円の価値があるコイン。"),
          Util.component("&dサーバー内で10000円として使用できる。"))
      .customModelData(5).glow();

  /**
   * アイテムがお金のアイテム化を判別します
   * @param itemStack 判別するアイテム
   * @return アイテムかどうか
   */
  public static boolean isMoneyItem(ItemStack itemStack) {
    return itemStack.getType().equals(Material.IRON_NUGGET) && itemStack.getItemMeta()
        .hasCustomModelData() && itemStack.getItemMeta().getCustomModelData() < 6;
  }

  /**
   * 渡されたアイテムの金額を計算します
   * @param itemStack お金のアイテム
   * @return このアイテムの金額
   */
  public static int getMoneyOfItem(ItemStack itemStack) {
    if (!isMoneyItem(itemStack)) {
      return 0;
    }
    return itemStack.getAmount() * switch (itemStack.getItemMeta().getCustomModelData()) {
      case 1 -> 1;
      case 2 -> 10;
      case 3 -> 100;
      case 4 -> 1000;
      case 5 -> 10000;
      default -> 0;
    };
  }
}
