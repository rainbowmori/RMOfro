package github.rainbowmori.ofro.object.config.rtp;

import github.rainbowmori.ofro.constants.OfroPrefix;
import github.rainbowmori.rainbowapi.util.FormatterUtil;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class RTPMessage {

  public final String title;
  public final String subtitle;
  public final String otherMessage;
  public final int seconds;
  public final boolean sendMessage;

  public RTPMessage(ConfigurationSection section) {
    if (section == null) {
      this.title = "";
      this.subtitle = "";
      this.otherMessage = "";
      this.seconds = 3;
      this.sendMessage = false;
      return;
    }
    this.title = section.getString("Title", "");
    this.subtitle = section.getString("SubTitle", "");
    this.otherMessage = section.getString("OtherMessage", "");
    this.seconds = section.getInt("Seconds", 3);
    this.sendMessage = section.getBoolean("SendMessage", false);
  }

  public RTPMessage(String title, String subtitle, String otherMessage, int seconds,
      boolean sendMessage) {
    this.title = title;
    this.subtitle = subtitle;
    this.otherMessage = otherMessage;
    this.seconds = seconds;
    this.sendMessage = sendMessage;
  }

  public static String format(String str, Player player, Location location) {
    return FormatterUtil.format(str,
        Map.of("x", location.getBlockX(), "y", location.getBlockY(), "z", location.getBlockZ(), "world",
            location.getWorld().getName(),"player", player.getName()));
  }

  public static String formatPlayer(String str, Player player) {
    return FormatterUtil.format(str, Map.of("player", player.getName()));
  }

  public void send(Player player,CommandSender other) {
    String formatTitle = formatPlayer(title, player);
    String formatSubTitle = formatPlayer(subtitle, player);
    Util.title(player, formatTitle, formatSubTitle, seconds);
    if (sendMessage) {
      if (!title.isEmpty()) {
        OfroPrefix.TP.send(player, formatTitle);
      }
      if (!subtitle.isEmpty()) {
        OfroPrefix.TP.send(player, formatSubTitle);
      }
    }
    if (player != other) {
      Util.send(other,formatPlayer(otherMessage,player));
    }
  }

  public void sendLocation(Player player,CommandSender other, Location location) {
    String formatTitle = format(title, player, location);
    String formatSubTitle = format(subtitle, player, location);
    Util.title(player, formatTitle, formatSubTitle, seconds);
    if (sendMessage) {
      if (!title.isEmpty()) {
        OfroPrefix.TP.send(player, formatTitle);
      }
      if (!subtitle.isEmpty()) {
        OfroPrefix.TP.send(player, formatSubTitle);
      }
    }
    if (player != other) {
      Util.send(other,format(otherMessage,player,location));
    }
  }
}
