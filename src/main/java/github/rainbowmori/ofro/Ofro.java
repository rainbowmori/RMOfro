package github.rainbowmori.ofro;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import github.rainbowmori.ofro.command.ItemEditCommand;
import github.rainbowmori.ofro.command.LobbyCommand;
import github.rainbowmori.ofro.command.ofro.OfroCommand;
import github.rainbowmori.ofro.listener.EventListeners;
import github.rainbowmori.ofro.listener.MVTeleportEvents;
import github.rainbowmori.ofro.listener.PlayerJoinQuitEvents;
import github.rainbowmori.ofro.listener.UseBed;
import github.rainbowmori.ofro.listener.WorldChange;
import github.rainbowmori.ofro.object.config.ConfigService;
import github.rainbowmori.ofro.object.customblock.model.RemoteMaterialTransmitterBlock;
import github.rainbowmori.ofro.object.customblock.model.ResourceVaultBlock;
import github.rainbowmori.ofro.object.customitem.RemoteMaterialTransmitterItem;
import github.rainbowmori.ofro.object.customitem.ResourceVaultItem;
import github.rainbowmori.ofro.object.customitem.savage.HideAmpouleItem;
import github.rainbowmori.ofro.object.customitem.savage.OfroAlertItem;
import github.rainbowmori.ofro.object.customitem.savage.OfroSonerItem;
import github.rainbowmori.ofro.object.multiverse.CommandDestination;
import github.rainbowmori.ofro.object.savage.SavageWorld;
import github.rainbowmori.rainbowapi.RMPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Ofro extends RMPlugin {

  private static ConfigService configService;
  private static MultiverseNetherPortals netherPortals;
  private static MultiverseInventories inventories;
  private static MultiverseCore core;
  private static Economy econ = null;

  public static ConfigService getConfigService() {
    return configService;
  }

  public static void initConfigService() {
    configService = new ConfigService();
  }

  public static Ofro getPlugin() {
    return getPlugin(Ofro.class);
  }

  public static MultiverseNetherPortals getNetherPortals() {
    return netherPortals;
  }

  public static MultiverseInventories getInventories() {
    return inventories;
  }

  public static MultiverseCore getCore() {
    return core;
  }

  public static Economy getEconomy() {
    return econ;
  }

  @Override
  public void onEnable() {
    getPrefixUtil().logInfo(String.format("Enabled Version %s", getDescription().getVersion()));
    if (!setupEconomy()) {
      getPrefixUtil().logInfo(
          String.format("[%s] - Disabled due to no Vault dependency found!", getName()));
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    initConfigService();

    customRegister();
    loadMultiverse();

    registerEvent(new UseBed());
    registerEvent(new MVTeleportEvents());
    registerEvent(new PlayerJoinQuitEvents());
    registerEvent(new WorldChange());
    registerEvent(new EventListeners());

    registerCommand(new LobbyCommand());

    registerCommand(new ItemEditCommand());

    registerCommand(new OfroCommand());

    saveResource("README.txt", true);
  }

  private void customRegister() {
    registerItem("HideAmpouleItem",HideAmpouleItem.class);
    registerItem("OfroAlertItem", OfroAlertItem.class);
    registerItem("OfroSonerItem", OfroSonerItem.class);

    registerItem("RemoteMaterialTransmitterItem", RemoteMaterialTransmitterItem.class);
    registerItem("ResourceVaultItem", ResourceVaultItem.class);
    registerBlock("RemoteMaterialTransmitterBlock", RemoteMaterialTransmitterBlock.class);
    registerBlock("ResourceVaultBlock", ResourceVaultBlock.class);
  }

  private void loadMultiverse() {
    netherPortals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager()
        .getPlugin("Multiverse-NetherPortals");
    inventories = (MultiverseInventories) Bukkit.getServer().getPluginManager()
        .getPlugin("Multiverse-Inventories");
    core = (MultiverseCore) Bukkit.getServer().getPluginManager()
        .getPlugin("Multiverse-Core");
    core.getDestFactory().registerDestinationType(CommandDestination.class, "cmd");
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return true;
  }

  @Override
  public void onDisable() {
    getPrefixUtil().logInfo(String.format("Disabled Version %s", getDescription().getVersion()));
    getConfigService().save();
    SavageWorld.saves();
  }

  @Override
  public String getPrefix() {
    return "<gold>[<aqua>Ofro♨Server<gold>]";
  }

  public static String getOfroMark() {
    return "♨";
  }
}
