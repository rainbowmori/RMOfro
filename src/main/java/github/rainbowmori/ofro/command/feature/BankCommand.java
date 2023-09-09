package github.rainbowmori.ofro.command.feature;

import github.rainbowmori.ofro.Ofro;
import github.rainbowmori.ofro.constants.Items;
import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.rainbowapi.dependencies.commandapi.CommandAPICommand;
import github.rainbowmori.rainbowapi.object.playerinput.PlayerChatInput;
import github.rainbowmori.rainbowapi.dependencies.ui.GuiInventoryHolder;
import github.rainbowmori.rainbowapi.dependencies.ui.button.ItemButton;
import github.rainbowmori.rainbowapi.dependencies.ui.button.RedirectItemButton;
import github.rainbowmori.rainbowapi.dependencies.ui.mask.Mask;
import github.rainbowmori.rainbowapi.dependencies.ui.mask.Pattern;
import github.rainbowmori.rainbowapi.dependencies.ui.menu.MenuHolder;
import github.rainbowmori.rainbowapi.dependencies.ui.menu.YesNoMenu;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;

import java.util.Map;
import java.util.Optional;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//TODO 他口座振込では1000円未満の送金の際、600円の手数料が発生する
public class BankCommand extends CommandAPICommand {

    public BankCommand() {
        super("bank");
        executesPlayer((sender, args) -> {
            openBank(sender);
        });
    }

    public static void openBank(Player sender) {
        sender.openInventory(new ATMHome(Ofro.getPlugin()).getInventory());
    }

    private static final class ATMHome extends MenuHolder<Ofro> {

        public ATMHome(Ofro plugin) {
            super(plugin, InventoryType.HOPPER,
                    "<aqua>Ofro" + Ofro.getOfroMark() + "<blue>ATM <gray>- <light_purple><bold>ホーム");
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
            Player player = (Player) event.getPlayer();
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
            setButton(0, new ItemButton<>(new ItemBuilder(ItemBuilder.createSkull(player.getName())).name(
                            "<aqua>%s<gold>の口座情報:".formatted(player.getName()))
                    .addLore("<light_purple>残高... <yellow><u>%s円".formatted(
                            ((int) Ofro.getEconomy().getBalance(player)))).build()));
            setButton(2, new RedirectItemButton<>(
                    new ItemBuilder(Material.CHEST).name("<yellow><bold>現金引き下ろし・入金")
                            .addLore("<gold><bold>現金←→電子マネーの変換はこちら!").build(),
                    () -> new ATM(getPlugin()).getInventory()));
            setButton(4, new RedirectItemButton<>(
                    new ItemBuilder(Material.DISPENSER).glow().name("<gold><bold>他口座振込")
                            .addLore("<white>\uE003<red><bold>振り込め詐欺にご注意を！<white>\uE003").build(),
                    () -> new TransferATM(getPlugin()).getInventory()));
        }

    }

    private static final class ATM extends MenuHolder<Ofro> {

        public ATM(Ofro plugin) {
            super(plugin, InventoryType.HOPPER,
                    "<aqua>Ofro" + Ofro.getOfroMark()
                            + "<blue>ATM <gray>- <light_purple><bold>引き下ろし・入金選択");
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
            Player player = ((Player) event.getPlayer());
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
            setButton(1, new RedirectItemButton<>(
                    new ItemBuilder(Material.DISPENSER).name("<yellow><bold>現金の引き下ろし").build(),
                    () -> new PullingDownATM(getPlugin()).getInventory()));
            setButton(2,
                    new ItemButton<>(new ItemBuilder(Material.PAPER).name("<green>どちらを実行しますか?").build()));
            setButton(3, new RedirectItemButton<>(
                    new ItemBuilder(Material.HOPPER).name("<gold><bold>現金の入金").build(),
                    () -> new PaymentATM(getPlugin()).getInventory()));
        }
    }

    private static final class PaymentATM extends GuiInventoryHolder<Ofro> {

        public PaymentATM(Ofro plugin) {
            super(plugin, 54,
                    "<aqua>Ofro" + Ofro.getOfroMark() + "<blue>ATM <gray>- <light_purple><bold>入金");
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            event.setCancelled(false);
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
            Player player = ((Player) event.getPlayer());
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
        }

        @Override
        public void onClose(InventoryCloseEvent event) {
            Player player = (Player) event.getPlayer();
            Economy economy = Ofro.getEconomy();
            int payment = 0;
            for (ItemStack stack : event.getInventory().getContents()) {
                if (stack == null) {
                    continue;
                }
                if (!Items.isMoneyItem(stack)) {
                    player.getInventory().addItem(stack);
                    continue;
                }
                int moneyItemAmount = Items.getMoneyOfItem(stack);
                payment += moneyItemAmount;
                economy.depositPlayer(player, moneyItemAmount);
            }
            if (payment != 0) {
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                OfroPrefix.BANK.send(player, "<yellow><u>%s円<reset><green>入金されました。".formatted(payment));
            }
        }
    }


    private static final class PullingDownATM extends MenuHolder<Ofro> {

        public PullingDownATM(Ofro plugin) {
            super(plugin, InventoryType.HOPPER,
                    "<aqua>Ofro" + Ofro.getOfroMark() + "<blue>ATM <gray>- <light_purple><bold>引き下ろし");
        }

        private static void hasMoney(Player player, int money, ItemStack item) {
            if (Ofro.getEconomy().has(player, money)) {
                Ofro.getEconomy().withdrawPlayer(player, money);
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                player.getInventory().addItem(item);
            } else {
                OfroPrefix.BANK.send(player, "<red>残高が足りません!");
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
            Player player = (Player) event.getPlayer();
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
            setButton(0, new ItemButton<>(Items.get1Yen()) {
                @Override
                public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
                    hasMoney(((Player) event.getWhoClicked()), 1, Items.get1Yen());
                }
            });
            setButton(1, new ItemButton<>(Items.get10Yen()) {
                @Override
                public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
                    hasMoney(((Player) event.getWhoClicked()), 10, Items.get10Yen());
                }
            });
            setButton(2, new ItemButton<>(Items.get100Yen()) {
                @Override
                public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
                    hasMoney(((Player) event.getWhoClicked()), 100, Items.get100Yen());
                }
            });
            setButton(3, new ItemButton<>(Items.get1000Yen()) {
                @Override
                public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
                    hasMoney(((Player) event.getWhoClicked()), 1000, Items.get1000Yen());
                }
            });
            setButton(4, new ItemButton<>(Items.get10000Yen()) {
                @Override
                public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
                    hasMoney(((Player) event.getWhoClicked()), 10000, Items.get10000Yen());
                }
            });
        }
    }

    private static final class TransferATM extends MenuHolder<Ofro> {

        private String money = "0";

        public TransferATM(Ofro plugin) {
            super(plugin, 54,
                    "<aqua>Ofro" + Ofro.getOfroMark() + "<blue>ATM <gray>- <light_purple><bold>金額入力");
        }

        private static ItemStack getPanel(int number) {
            return new ItemBuilder(Material.IRON_INGOT).name("<white><bold>" + number)
                    .customModelData(1000 + number).build();
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
            Player player = (Player) event.getPlayer();
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
            Pattern.ofGrid("" +
                    "000000000" +
                    "111111111" +
                    "100012221" +
                    "100012221" +
                    "100012221" +
                    "100011111"
            ).applyInventory(Mask.ofMap(
                            Map.of('1', new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("").build(), '2',
                                    new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("").build())),
                    getInventory());
            setButton(19, createButton(1));
            setButton(20, createButton(2));
            setButton(21, createButton(3));
            setButton(28, createButton(4));
            setButton(29, createButton(5));
            setButton(30, createButton(6));
            setButton(37, createButton(7));
            setButton(38, createButton(8));
            setButton(39, createButton(9));
            setButton(46, new ItemButton<>(
                    new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("<green><bold>決定").build()) {
                @Override
                public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    int i = Integer.parseInt(money);
                    if (i < 1) {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        OfroPrefix.BANK.send(player, "<red>最低1円以上の値を入力してください");
                        return;
                    }
                    if (!Ofro.getEconomy().has(player, i)) {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        OfroPrefix.BANK.send(player, "<red>残高が不足しているため、金額が承認されませんでした。");
                        return;
                    }
                    player.closeInventory();
                    if (PlayerChatInput.isInputted(player)) {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        OfroPrefix.BANK.send(player, "<red>すでにチャット入力を開始していますそれを終わらせてからにしてください");
                        return;
                    }
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                    new PlayerChatInput(getPlugin(),
                            Util.cc(Util.serializeLegacy(OfroPrefix.BANK.getPrefix())), true,
                            new StringPrompt() {
                                @Override
                                public @NotNull String getPromptText(@NotNull ConversationContext context) {
                                    return Util.cc(
                                            "&d20秒以内に送金先のプレイヤー名を入力してください&7(\"cancel\"と入力するとキャンセルできます)");
                                }

                                @Override
                                public @Nullable Prompt acceptInput(@NotNull ConversationContext context,
                                                                    @Nullable String input) {
                                    Player destination;
                                    if (input == null || (destination = Bukkit.getPlayer(input)) == null) {
                                        OfroPrefix.BANK.send(player, "<red>送金先のアカウントが見つかりませんでした。");
                                        OfroPrefix.BANK.send(player, "<red>お手数ですが最初からやり直してください。");
                                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                                        return null;
                                    }
                                    if (player.equals(destination)) {
                                        OfroPrefix.BANK.send(player, "<red>自分のアカウントに送金することはできません!");
                                        OfroPrefix.BANK.send(player, "<red>お手数ですが最初からやり直してください。");
                                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                                        return null;
                                    }
                                    player.openInventory(new YesNoMenu<>(getPlugin(),
                                            "<aqua>Ofro" + Ofro.getOfroMark() + "<blue>ATM <gray>- 送金確認", e -> {
                                        Economy economy = Ofro.getEconomy();
                                        economy.withdrawPlayer(player, i);
                                        economy.depositPlayer(destination, i);
                                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        player.playSound(destination, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        OfroPrefix.BANK.send(player,
                                                "<aqua><bold>%s<reset><green>さんに<yellow><u>%s円<reset><green>送金しました!".formatted(
                                                        destination.getName(), money));
                                        OfroPrefix.BANK.send(destination,
                                                "<aqua><bold>%s<reset><green>さんから<yellow><u>%s円<reset><green>送金されました!".formatted(
                                                        player.getName(), money));
                                    }, e -> {
                                        Player whoClicked = (Player) e.getWhoClicked();
                                        whoClicked.playSound(whoClicked, Sound.ENTITY_VILLAGER_NO, 1, 1);
                                        OfroPrefix.BANK.send(player, "<red>取引がキャンセルされました。");
                                    }) {
                                        @Override
                                        protected @NotNull ItemStack getYesStack() {
                                            return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name(
                                                    "<green><bold>送金する").build();
                                        }

                                        @Override
                                        protected @NotNull ItemStack getNoStack() {
                                            return new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(
                                                    "<red><bold>キャンセル").build();
                                        }

                                        @Override
                                        public Optional<ItemStack> getMiddleItem() {
                                            return Optional.of(
                                                    new ItemBuilder(ItemBuilder.createSkull(destination.getName())).name(
                                                                    "<aqua><bold>" + destination.getName())
                                                            .addLore("<gold><bold>送金金額... <reset><yellow><u>" + money + "円")
                                                            .build());
                                        }
                                    }.getInventory());
                                    return null;
                                }
                            }, 20).build(player);
                }
            });
            setButton(47, createButton(0));
            setButton(48, new ItemButton<>(
                    new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("<red><bold>リセット").build()) {
                @Override
                public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                    money = "0";
                    for (int i = 0; i < 9; i++) {
                        unsetButton(i);
                    }
                    reload();
                }
            });
            reload();
        }

        private ItemButton<TransferATM> createButton(int i) {
            return new ItemButton<>(getPanel(i)) {
                @Override
                public void onClick(TransferATM holder, InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                    if (!addNumber(i)) {
                        OfroPrefix.BANK.send(event.getWhoClicked(), "<red>これ以上は入力できません");
                    }
                }
            };
        }

        private boolean addNumber(int i) {
            money = money.replaceFirst("^0+", "");
            if (money.length() > 8) {
                return false;
            }
            money = money + i;
            reload();
            return true;
        }

        private void reload() {
            int length = money.length();

            for (int i = length - 1; i >= 0; i--) {
                char c = money.charAt(i);
                setButton(9 - (length - i), new ItemButton<>(getPanel(Character.getNumericValue(c))));
            }
        }
    }

}
