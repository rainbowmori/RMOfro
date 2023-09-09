package github.rainbowmori.ofro.object.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.destroystokyo.paper.profile.ProfileProperty;
import github.rainbowmori.ofro.Ofro;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class QuitPlayer {

  private final Location location;
  private final UUID playerUUID;
  private final UUID npcUUID;
  private final int npcID;
  private final List<ItemStack> itemStacks;
  private final long currentTime;

  public QuitPlayer(Player player) {
    this.location = player.getLocation();
    this.playerUUID = player.getUniqueId();
    this.npcUUID = UUID.randomUUID();
    this.npcID = Bukkit.getUnsafe().nextEntityId();
    List<ItemStack> itemStacks = new java.util.ArrayList<>(
        Arrays.stream(player.getInventory().getContents())
            .toList());
    itemStacks.addAll(Arrays.stream(player.getInventory().getArmorContents()).toList());
    this.itemStacks = itemStacks;
    this.currentTime = System.currentTimeMillis();
    quit(player);
  }

  private void quit(Player player) {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    WrappedGameProfile gameProfile = new WrappedGameProfile(npcUUID, player.getName());
    ProfileProperty profileProperty = player.getPlayerProfile().getProperties()
        .toArray(ProfileProperty[]::new)[0];
    gameProfile.getProperties()
        .put("textures", WrappedSignedProperty.fromValues("textures", profileProperty.getValue(),
            profileProperty.getSignature()));

    PacketContainer packet = protocolManager.createPacket(Server.PLAYER_INFO);

    packet.getPlayerInfoActions()
        .write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
    packet.getPlayerInfoDataLists().write(1, Collections.singletonList(
        new PlayerInfoData(gameProfile, 0, NativeGameMode.CREATIVE,
            WrappedChatComponent.fromText(
                player.getName()))
    ));

    PacketContainer spawnPlayer = protocolManager
        .createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
    spawnPlayer.getIntegers().write(0, npcID);
    spawnPlayer.getUUIDs().write(0, npcUUID);
    spawnPlayer.getDoubles().write(0, location.getX());
    spawnPlayer.getDoubles().write(1, location.getY());
    spawnPlayer.getDoubles().write(2, location.getZ());
    spawnPlayer.getBytes().write(0, (byte) (((location.getYaw() * 256.0F) / 360.0F)));
    spawnPlayer.getBytes().write(1, (byte) (((location.getPitch() * 256.0F) / 360.0F)));

    PacketContainer metadata = protocolManager.createPacket(Server.ENTITY_METADATA);
    metadata.getIntegers().write(0, npcID);
    Serializer serializer = Registry.get(Byte.class);
    WrappedDataValue wrappedDataValue = new WrappedDataValue(17, serializer,
        (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
    metadata.getDataValueCollectionModifier().write(0, List.of(wrappedDataValue));

    protocolManager.broadcastServerPacket(packet);
    protocolManager.broadcastServerPacket(spawnPlayer);
    protocolManager.broadcastServerPacket(metadata);
    Bukkit.getScheduler().runTaskLater(Ofro.getPlugin(), () -> {
      if (Ofro.getManagerService().isQuitPlayer(playerUUID)) {
        Ofro.getManagerService().removeQuitPlayer(playerUUID);
        PacketContainer destroy = protocolManager
            .createPacket(Server.ENTITY_DESTROY);
        destroy.getIntLists().write(0, List.of(npcID));
        PacketContainer remove = protocolManager
            .createPacket(Server.PLAYER_INFO_REMOVE);
        remove.getUUIDLists().write(0, Collections.singletonList(npcUUID));
        protocolManager.broadcastServerPacket(destroy);
        protocolManager.broadcastServerPacket(remove);
      }
    }, 200L);
  }

  public long currentTime() {
    return currentTime;
  }

  public Location location() {
    return location;
  }

  public UUID playerUUID() {
    return playerUUID;
  }

  public UUID npcUUID() {
    return npcUUID;
  }

  public int npcID() {
    return npcID;
  }

  public List<ItemStack> itemStacks() {
    return itemStacks;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (QuitPlayer) obj;
    return Objects.equals(this.location, that.location) &&
        Objects.equals(this.playerUUID, that.playerUUID) &&
        Objects.equals(this.npcUUID, that.npcUUID) &&
        this.npcID == that.npcID &&
        Objects.equals(this.itemStacks, that.itemStacks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, playerUUID, npcUUID, npcID, itemStacks);
  }

  @Override
  public String toString() {
    return "QuitPlayer[" +
        "location=" + location + ", " +
        "playerUUID=" + playerUUID + ", " +
        "npcUUID=" + npcUUID + ", " +
        "npcID=" + npcID + ", " +
        "itemStacks=" + itemStacks + ']';
  }


}