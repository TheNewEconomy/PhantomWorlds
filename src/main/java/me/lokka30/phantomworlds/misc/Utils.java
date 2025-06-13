package me.lokka30.phantomworlds.misc;

import me.lokka30.microlib.messaging.MessageUtils;
import me.lokka30.microlib.messaging.MultiMessage;
import me.lokka30.phantomworlds.PhantomWorlds;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class contains Utility methods which are public & static which are used by multiple classes.
 * If a method is only used by one class then it is advised to keep it in the class to avoid
 * bloating this class.
 *
 * @author lokka30
 * @since v2.0.0
 */
public class Utils {

  /**
   * This is used for tab completion where numbers are expected, for example, coordinates in the
   * setspawn subcommand.
   */
  public static final List<String> ZERO_THRU_NINE = Arrays.asList("0", "1", "2", "3", "4", "5",
                                                                  "6", "7", "8", "9");

  /**
   * This method returns a list of the names of worlds that are loaded on the server. Used in tab
   * completion, for example.
   *
   * @return set of world names
   *
   * @since v2.0.0
   */
  public static HashSet<String> getLoadedWorldsNameList() {

    final HashSet<String> loadedWorlds = new HashSet<>();
    Bukkit.getWorlds().forEach(world->loadedWorlds.add(world.getName()));
    return loadedWorlds;
  }

  public static boolean copyFolder(final Path source, final Path target) throws IOException {

    if(!Files.exists(source) || !Files.isDirectory(source)) {
      return false;
    }

    // Create the target directory if it doesn't exist
    if(!Files.exists(target)) {
      Files.createDirectories(target);
    }

    // Walk the file tree from the source directory and copy each file/folder
    try(final Stream<Path> pathStream = Files.walk(source)) {

      pathStream.forEach(sourcePath->{
        final Path targetPath = target.resolve(source.relativize(sourcePath));
        try {

          if(!targetPath.toString().toLowerCase(Locale.ROOT).contains("uid.dat")
             && !targetPath.toString().toLowerCase(Locale.ROOT).contains("session.lock")) {

            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
          }
        } catch(final FileSystemException ignore) {
          //session.lock is lame
        } catch(final IOException e) {

          throw new RuntimeException("Failed to copy file: " + sourcePath, e);
        }
      });
    }
    return true;
  }

  /**
   * Attempts to register specified command. Sends status to console as logs.
   *
   * @param clazz   CommandExecutor to be registered
   * @param command Name of the command as stated in plugin.yml
   *
   * @since v2.0.0
   */
  public static void registerCommand(@NotNull final CommandExecutor clazz, @NotNull final String command) {

    if(PhantomWorlds.instance().getCommand(command) == null) {
      PhantomWorlds.logger().severe("Unable to register command '/" + command + "' - PluginCommand "
                                    + "is null. Was plugin.yml tampered with?");
    } else {
      //noinspection ConstantConditions
      PhantomWorlds.instance().getCommand(command).setExecutor(clazz);
      PhantomWorlds.logger().info("Registered command '/" + command + "'.");
    }
  }

  /**
   * Tells the server to unload specified world so it can be deleted. Additionally: -> Kicks all
   * players from it before unloading. -> It does not transfer users to other worlds for security
   * purposes. This may be changed in the future.
   *
   * @param world World to be unloaded
   *
   * @since v2.0.0
   */
  public static void unloadWorld(@NotNull final World world) {
    // inform console
    PhantomWorlds.logger().info(String.format(
            "Unloading world %s; kicking %s players from the world...",
            world.getName(),
            world.getPlayers().size()
                                             ));

    // kick players in world
    // using an iterator to avoid a possible ConcurrentModificationException
    world.getPlayers().iterator().forEachRemaining(player->
                                                           // yikes, this gets messy. :P
                                                           player.kickPlayer(MessageUtils.colorizeAll(
                                                                   String.join("\n",
                                                                               PhantomWorlds.instance().messages.getConfig()
                                                                                       .getStringList("command.phantomworlds.subcommands.unload.kick")
                                                                              )
                                                                           .replace("%prefix%",
                                                                                    PhantomWorlds.instance().messages.getConfig()
                                                                                            .getString("common.prefix", "PhantomWorlds: "))
                                                                           .replace("%world%", world.getName())
                                                                                                     ))
                                                  );

    // time to unload the world
    Bukkit.unloadWorld(world, true);
  }

  /**
   * For the CommandSender specified, this method will list every player that the tab list will show
   * them. This does not work with vanish plugins that **exclusively** use packets, as it relies on
   * Bukkit's 'hidePlayer' system.
   *
   * @param sender commandsender to check. if console, all players are visible.
   *
   * @return list of usernames
   *
   * @since v2.0.0
   */
  public static List<String> getPlayersCanSeeList(@NotNull final CommandSender sender) {

    final List<String> suggestions = new ArrayList<>();

    if(!sender.hasPermission("phantomworlds.knows-vanished-users")
       && sender instanceof Player) {
      final Player player = (Player)sender;
      for(final Player listedPlayer : Bukkit.getOnlinePlayers()) {
        if(player.canSee(listedPlayer)) {
          suggestions.add(listedPlayer.getName());
        }
      }
    } else {
      for(final Player listedPlayer : Bukkit.getOnlinePlayers()) {
        suggestions.add(listedPlayer.getName());
      }
    }

    return suggestions;
  }

  /**
   * @param values Enum#values() call
   *
   * @return a list of string conversions of each enum value
   *
   * @since v2.0.0
   */
  public static List<String> enumValuesToStringList(final Object[] values) {

    final List<String> strings = new ArrayList<>();
    for(final Object value : values) {
      strings.add(value.toString());
    }
    return strings;
  }

  /**
   * Credit: <a href="https://stackoverflow.com/q/11701399">StackOverflow</a>
   *
   * @param val value to round
   *
   * @return val, rounded to 2 decimal places.
   */
  public static double roundTwoDecimalPlaces(final double val) {

    return Math.round(val * 100) / 100.0;
  }

  public static Optional<Boolean> parseFromString(final CommandSender sender, final StringBuilder value, final String option) {

    switch(value.toString().toLowerCase(Locale.ROOT)) {
      case "false":
      case "f":
      case "no":
      case "n":
        return Optional.of(false);
      case "true":
      case "t":
      case "yes":
      case "y":
        return Optional.of(true);
      default:
        (new MultiMessage(
                PhantomWorlds.instance().messages.getConfig().getStringList(
                        "command.phantomworlds.subcommands.create.options.invalid-value"),
                Arrays.asList(
                        new MultiMessage.Placeholder("prefix",
                                                     PhantomWorlds.instance().messages.getConfig().getString("common.prefix",
                                                                                                             "&b&lPhantomWorlds: &7"), true),
                        new MultiMessage.Placeholder("value", value.toString(),
                                                     false),
                        new MultiMessage.Placeholder("option", option, false),
                        new MultiMessage.Placeholder("expected",
                                                     "Boolean (true/false)", false)
                             ))).send(sender);
        return Optional.empty();
    }
  }

  public static void zipFolder(final File sourceFolder, final String destinationZipFile) throws IOException {

    try(final FileOutputStream fos = new FileOutputStream(destinationZipFile);
        final ZipOutputStream zos = new ZipOutputStream(fos)) {

      //zipFolder(sourceFolder, sourceFolder.getName(), zos);

      zipFile(sourceFolder, sourceFolder.getName(), zos);
      zos.closeEntry();
      zos.flush();
      zos.close();
      fos.flush();
    } catch(final Exception e) {
      e.printStackTrace();
    }
  }

  public static void zipFile(final File fileToZip, final String fileName, final ZipOutputStream zipOut) throws IOException {

    if(fileToZip.isHidden()) {
      return;
    }

    if(fileToZip.isDirectory()) {

      if(fileName.endsWith("/")) {

        zipOut.putNextEntry(new ZipEntry(fileName));
        zipOut.closeEntry();
      } else {

        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
        zipOut.closeEntry();
      }

      final File[] children = fileToZip.listFiles();
      if(children == null) {
        return;
      }

      for(final File childFile : children) {
        zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
      }
      return;
    }

    final FileInputStream fis = new FileInputStream(fileToZip);
    final ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);

    final byte[] bytes = new byte[1024];
    int length;

    while((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
    fis.close();
  }

  public static void zipFolder(final File folder, final String parentFolder, final ZipOutputStream zos) throws IOException {

    if(folder == null || folder.exists()) {
      return;
    }

    final File[] files = folder.listFiles();
    if(files == null) {
      return;
    }

    for(final File file : files) {
      if(file.isDirectory()) {
        zipFolder(file, parentFolder + File.separator + file.getName(), zos);
        continue;
      }

      final ZipEntry zipEntry = new ZipEntry(parentFolder + File.separator + file.getName());
      zos.putNextEntry(zipEntry);

      try(final FileInputStream fis = new FileInputStream(file)) {
        final byte[] buffer = new byte[1024];
        int length;
        while((length = fis.read(buffer)) > 0) {
          zos.write(buffer, 0, length);
        }
      }
      zos.closeEntry();
    }
  }

  public static boolean deleteFolder(final File folder) {

    if(folder.exists()) {
      final File[] files = folder.listFiles();
      if(files != null) {
        for(final File file : files) {
          if(file.isDirectory()) {
            deleteFolder(file);
          } else {
            file.delete();
          }
        }
      }
    }
    return folder.delete();
  }

  public static void teleportToWorld(@NotNull final CommandSender sender, @NotNull final String subCommand,
                                     @NotNull final String label, @Nullable final String targetPlayerName,
                                     @Nullable String worldName) {

    final Player targetPlayer;
    if(targetPlayerName != null && !(sender instanceof Player)
       || targetPlayerName != null && sender.hasPermission("phantomworlds.command.phantomworlds.teleport.other")) {

      targetPlayer = Bukkit.getPlayer(targetPlayerName);

      // If the target is offline or invisible to the sender, then stop
      if(targetPlayer == null || !Utils.getPlayersCanSeeList(sender)
              .contains(targetPlayer.getName())) {
        (new MultiMessage(
                PhantomWorlds.instance().messages.getConfig()
                        .getStringList("command.phantomworlds.subcommands.common.player-offline"),
                Arrays.asList(
                        new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                                .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                        new MultiMessage.Placeholder("player", targetPlayerName, false)
                             ))).send(sender);
        return;
      }
    } else {
      if(sender instanceof Player) {

        targetPlayer = (Player)sender;
      } else {

        (new MultiMessage(
                PhantomWorlds.instance().messages.getConfig().getStringList(
                        "command.phantomworlds.subcommands." + subCommand + ".usage-console"),
                Arrays.asList(
                        new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                                .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                        new MultiMessage.Placeholder("label", label, false)
                             ))).send(sender);
        return;
      }
    }

    if(worldName == null) {
      worldName = targetPlayer.getWorld().getName();
    }

    final World world = Bukkit.getWorld(worldName);
    if(world == null) {
      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.subcommands.common.invalid-world"),
              Arrays.asList(
                      new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                              .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                      new MultiMessage.Placeholder("world", worldName, false)
                           ))).send(sender);
      return;
    }

    PhantomWorlds.worldManager().tpAwaiting.put(targetPlayer.getUniqueId(), new MultiMessage(
            PhantomWorlds.instance().messages.getConfig()
                    .getStringList("command.phantomworlds.subcommands." + subCommand + ".success"),
            Arrays.asList(
                    new MultiMessage.Placeholder("prefix",
                                                 PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                                 true),
                    new MultiMessage.Placeholder("player", targetPlayer.getName(), false),
                    new MultiMessage.Placeholder("world", worldName, false)
                         )));

    final String key = targetPlayer.getUniqueId() + "-" + world.getName();
    if(PhantomWorlds.instance().lastLocations().containsKey(key)) {

      System.out.println("sending to last location.");
      targetPlayer.teleport(PhantomWorlds.instance().lastLocations().get(key));
      return;
    }

    targetPlayer.teleport(parseSpawn(world));
  }

  public static Location parseSpawn(final World world) {

    final String cfgPath = "worlds-to-load." + world.getName() + ".spawn";
    if(PhantomWorlds.instance().data.getConfig().contains(cfgPath)) {
      final double x = PhantomWorlds.instance().data.getConfig().getDouble(cfgPath + ".x", world.getSpawnLocation().getX());
      final double y = PhantomWorlds.instance().data.getConfig().getDouble(cfgPath + ".y", world.getSpawnLocation().getY());
      final double z = PhantomWorlds.instance().data.getConfig().getDouble(cfgPath + ".z", world.getSpawnLocation().getZ());
      final float yaw = (float)PhantomWorlds.instance().data.getConfig().getDouble(cfgPath + ".yaw", world.getSpawnLocation().getYaw());
      final float pitch = (float)PhantomWorlds.instance().data.getConfig().getDouble(cfgPath + ".pitch", world.getSpawnLocation().getPitch());

      return new Location(world, x, y, z, yaw, pitch);
    }
    return world.getSpawnLocation();
  }

  public static boolean checkWorld(@NotNull final CommandSender sender, final String usage, @Nullable final World world) {

    if(world == null) {

      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList(usage), Arrays.asList(
              new MultiMessage.Placeholder("prefix",
                                           PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                           true),
              new MultiMessage.Placeholder("label", "pw", false)
                                                          ))).send(sender);

      return false;
    }
    return true;
  }

  public static void applyWorldEffects(final Player player, final String world) {

    final String cfgPath = "worlds-to-load." + world;
    
    if(PhantomWorlds.instance().data.getConfig().contains(cfgPath + ".gameMode") && !player.hasPermission("phantomworlds.world.bypass.gamemode")) {
      final GameMode mode = GameMode.valueOf(PhantomWorlds.instance().data.getConfig().getString(cfgPath + ".gameMode"));
      player.setGameMode(mode);
    }

    if(PhantomWorlds.instance().data.getConfig().contains(cfgPath + ".effects") &&
       PhantomWorlds.instance().data.getConfig().isConfigurationSection(cfgPath + ".effects") && !player.hasPermission("phantomworlds.world.bypass.effects")) {

      for(final String effName : PhantomWorlds.instance().data.getConfig().getConfigurationSection(cfgPath + ".effects").getKeys(false)) {
        final int duration = PhantomWorlds.instance().data.getConfig().getInt(cfgPath + ".effects." + effName + ".duration", -1);
        final int amplifier = PhantomWorlds.instance().data.getConfig().getInt(cfgPath + ".effects." + effName + ".amplifier", 1);

        final PotionEffectType type = PhantomWorlds.compatibility().findType(effName);
        if(type != null) {
          final PotionEffect effect = new PotionEffect(type, duration, amplifier);
          player.addPotionEffect(effect);
        }
      }
    }
  }

  public static String defaultWorld() {

    final String property = stripLeadingSlash(System.getProperty("serverProperties", "server.properties"));
    try(final InputStream input = Files.newInputStream(new File(PhantomWorlds.instance().getDataFolder(), "../../" + property).toPath())) {

      final Properties prop = new Properties();
      prop.load(input);

      return prop.getProperty("level-name");
    } catch(final Exception ignore) {
      return "world";
    }
  }

  public static String stripLeadingSlash(final String input) {
    final StringBuilder sb = new StringBuilder(input);
    while(sb.length() > 0 && sb.charAt(0) == '0') {

      sb.deleteCharAt(0);
    }
    return sb.toString();
  }

  public static boolean isOneSeventeen(final String version) {

    return version.contains("1.17") || version.contains("1.7") || version.contains("1.8") ||
           version.contains("1.9") || version.contains("1.10") || version.contains("1.11") ||
           version.contains("1.12") || version.contains("1.13") || version.contains("1.14") ||
           version.contains("1.15") || version.contains("1.16");
  }

  public static boolean isTwentyFive(final String version) {

    return version.contains("1.20.5");
  }
}