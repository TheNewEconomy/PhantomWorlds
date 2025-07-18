package me.lokka30.phantomworlds;

import com.tcoded.folialib.FoliaLib;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import me.lokka30.microlib.files.YamlConfigFile;
import me.lokka30.microlib.maths.QuickTimer;
import me.lokka30.phantomworlds.comatibility.VersionCompatibility;
import me.lokka30.phantomworlds.comatibility.impl.OneSeventeenCompatibility;
import me.lokka30.phantomworlds.comatibility.impl.OneTwentyCompatibility;
import me.lokka30.phantomworlds.commands.PWCommand;
import me.lokka30.phantomworlds.commands.handler.PWInvalidUsageHandler;
import me.lokka30.phantomworlds.commands.params.AliasWorldParameter;
import me.lokka30.phantomworlds.commands.params.GamemodeParameter;
import me.lokka30.phantomworlds.commands.params.PortalParameter;
import me.lokka30.phantomworlds.commands.params.PotionEffectParameter;
import me.lokka30.phantomworlds.commands.params.SettingParameter;
import me.lokka30.phantomworlds.commands.params.WorldFolderParameter;
import me.lokka30.phantomworlds.commands.utils.WorldFolder;
import me.lokka30.phantomworlds.listeners.player.PlayerChangeWorldListener;
import me.lokka30.phantomworlds.listeners.player.PlayerJoinListener;
import me.lokka30.phantomworlds.listeners.player.PlayerPortalListener;
import me.lokka30.phantomworlds.listeners.player.PlayerRespawnListener;
import me.lokka30.phantomworlds.listeners.player.PlayerTeleportListener;
import me.lokka30.phantomworlds.listeners.plugin.PluginEnableListener;
import me.lokka30.phantomworlds.listeners.world.WorldInitListener;
import me.lokka30.phantomworlds.managers.FileManager;
import me.lokka30.phantomworlds.managers.WorldManager;
import me.lokka30.phantomworlds.misc.CompatibilityChecker;
import me.lokka30.phantomworlds.misc.PAPIHook;
import me.lokka30.phantomworlds.misc.UpdateChecker;
import me.lokka30.phantomworlds.misc.UpdateCheckerResult;
import me.lokka30.phantomworlds.misc.Utils;
import me.lokka30.phantomworlds.scheduler.BackupScheduler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This is the main class of the PhantomWorlds plugin.
 *
 * @author lokka30
 * @since v2.0.0
 */
public class PhantomWorlds extends JavaPlugin {

  protected Map<String, Location> lastLocations = new ConcurrentHashMap<>();

  public static final List<String> createTabs = new ArrayList<>();

  /*
   *TODO:
   * - Translate backslash character in world names as a space so world names with a space can be used in the plugin
   * - Vanish compatibility
   *  - don't send 'by' messages unless the sender is not a player / target can see the (player) sender
   *  - add vanish compatibility to 'teleport' subcommand
   *  - add ability to toggle vanish compatibility
   * - log in console (LogLevel:INFO) when a command is prevented due to a target player seemingly being vanished to the command sender.
   */

  private static PhantomWorlds instance;

  private static VersionCompatibility compatibility;

  protected LiteCommands<?> command;
  protected FoliaLib folia;

  private BackupScheduler backupService = null;

  /**
   * If you have contributed code to the plugin, add your name to the end of this list! :)
   */
  public static final String[] CONTRIBUTORS = new String[]{ "madison-allen" };

  public static final List<String> COMMAND_HELP = new LinkedList<>();

  public static final String BACKUP_FOLDER = "backups";
  public static final String ARCHIVE_FOLDER = "archives";

  /**
   * This is reported in the 'pw info' command to inform the command sender of what MC versions that
   * this version of PW is designed to run on, and is therefore supported.
   */
  public final String supportedServerVersions = "1.7.x and newer";

  /**
   * Frequently used vars.
   */
  public final FileManager fileManager = new FileManager();
  public final WorldManager worldManager = new WorldManager();

  /**
   * Miscellaneous vars.
   */
  public final CompatibilityChecker compatibilityChecker = new CompatibilityChecker();
  public UpdateCheckerResult updateCheckerResult = null;

  /**
   * Data/configuration files.
   */
  public final YamlConfigFile settings = new YamlConfigFile(this,
                                                            new File(getDataFolder(), "settings.yml"));
  public final YamlConfigFile advancedSettings = new YamlConfigFile(this,
                                                                    new File(getDataFolder(), "advancedSettings.yml"));
  public final YamlConfigFile messages = new YamlConfigFile(this,
                                                            new File(getDataFolder(), "messages.yml"));
  public final YamlConfigFile data = new YamlConfigFile(this,
                                                        new File(getDataFolder(), "data.yml"));

  /*
      Used to check if world are loaded
   */
  private boolean isWorldLoaded = false;

  /**
   * This method is called by Bukkit when it loads PhantomWorlds.
   *
   * @since v2.0.0
   */
  @Override
  public void onEnable() {

    instance = this;

    getLogger().info("Starting up Placeholder Registration...");
    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new PAPIHook().register();
    } else {
      getServer().getPluginManager().registerEvents(new PluginEnableListener(), this);
    }
    createTabs.addAll(generateCreateSuggestions());

    final QuickTimer timer = new QuickTimer(TimeUnit.MILLISECONDS);

    final String bukkitVersion = Bukkit.getServer().getBukkitVersion();
    if(Utils.isOneSeventeen(bukkitVersion)) {
      compatibility = new OneSeventeenCompatibility();
    } else {
      compatibility = new OneTwentyCompatibility();
    }
    checkCompatibility();
    loadFiles();

    this.folia = new FoliaLib(this);

    registerCommands();
    registerListeners();
    miscStartupProcedures();

    if(settings.getConfig().getBoolean("backup-scheduler", true)) {
      getLogger().info("Starting up Backup scheduler...");
      backupService = new BackupScheduler();
      backupService.start(settings.getConfig().getInt("backup-delay", 600) * 20L, settings.getConfig().getInt("backup-delay", 600) * 20L);
    }

    getLogger().info("Start-up complete (took " + timer.getDuration() + "ms)");
  }

  public boolean isWorldLoaded() {

    return isWorldLoaded;
  }

  /**
   * This method is called by Bukkit when it disables PhantomWorlds.
   *
   * @since v2.0.0
   */
  @Override
  public void onDisable() {

    final QuickTimer timer = new QuickTimer(TimeUnit.MILLISECONDS);

    if(backupService != null) {
      getLogger().info("Shutting down backup scheduler...");
      backupService.stop();
    }

    getLogger().info("Shut-down complete (took " + timer.getDuration() + "ms)");
  }

  /**
   * Run the compatibility checkker. Reports in the console if it finds any possible issues.
   *
   * @since v2.0.0
   */
  void checkCompatibility() {

    getLogger().info("Checking compatibility with server...");

    compatibilityChecker.checkAll();

    if(compatibilityChecker.incompatibilities.isEmpty()) {
      return;
    }

    for(int i = 0; i < compatibilityChecker.incompatibilities.size(); i++) {
      final CompatibilityChecker.Incompatibility incompatibility = compatibilityChecker.incompatibilities.get(
              i);
      getLogger().warning(
              "Incompatibility #" + (i + 1) + " (Type: " + incompatibility.type + "):");
      getLogger().info(" -> Reason: " + incompatibility.reason);
      getLogger().info(" -> Recommendation: " + incompatibility.recommendation);
    }
  }

  /**
   * (Re)load all data/configuration files. Creates them if they don't exist. Applies version
   * checking where suitable.
   *
   * @since v2.0.0
   */
  public void loadFiles() {

    getLogger().info("Checking for backup directory...");

    final File backup = new File(getDataFolder(), BACKUP_FOLDER);
    if(!backup.exists()) {
      backup.mkdirs();
    }

    getLogger().info("Loading files...");

    for(final FileManager.PWFile pwFile : FileManager.PWFile.values()) {
      fileManager.init(pwFile);
    }
  }

  /**
   * Checks on the worlds that are created through PhantomWorlds. If they aren't already loaded, PW
   * loads them.
   *
   * @since v2.0.0
   */
  public void loadWorlds() {

    getLogger().info("Loading worlds...");
    worldManager.loadManagedWorlds();
    isWorldLoaded = true;
  }

  /**
   * Registers the commands for the plugin. In this case, only one command is registered (with an
   * array of sub-commands of course).
   *
   * @since v2.0.0
   */
  void registerCommands() {

    getLogger().info("Registering commands...");

    this.command = LiteBukkitFactory.builder()
            .commands(new PWCommand())
            .settings(settings->settings
                              .nativePermissions(false)
                     )
            .invalidUsage(new PWInvalidUsageHandler())
            .argument(GameMode.class, new GamemodeParameter())
            .argument(PortalType.class, new PortalParameter())
            .argument(String.class, ArgumentKey.of("world-setting"), new SettingParameter())
            .argument(String.class, ArgumentKey.of("potion-effects"), new PotionEffectParameter())
            .argument(World.class, new AliasWorldParameter())
            .argument(WorldFolder.class, new WorldFolderParameter()).build();
  }

  /**
   * Registers the listeners for the plugin. These classes run code when Events happen on the
   * server, e.g. 'player joins server' or 'player changes world'.
   *
   * @since v2.0.0
   */
  void registerListeners() {

    getLogger().info("Registering listeners...");
    getServer().getPluginManager().registerEvents(new PlayerChangeWorldListener(this), this);
    getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
    getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    getServer().getPluginManager().registerEvents(new PlayerPortalListener(this), this);
    getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);

    getServer().getPluginManager().registerEvents(new WorldInitListener(this), this);
  }

  /**
   * Miscellaneous startup procedures.
   *
   * @since v2.0.0
   */
  void miscStartupProcedures() {

    getLogger().info("Running misc startup procedures...");

    /* bStats Metrics */
    new Metrics(this, 8916);

    /* Update Checker */
    if(settings.getConfig().getBoolean("run-update-checker", true)) {
      try {
        final UpdateChecker updateChecker = new UpdateChecker(this, 84017);
        updateChecker.getLatestVersion(latestVersion->{
          updateCheckerResult = new UpdateCheckerResult(
                  !latestVersion.equals(updateChecker.getCurrentVersion()),
                  updateChecker.getCurrentVersion(),
                  latestVersion
          );

          if(updateCheckerResult.isOutdated()) {
            if(!messages.getConfig()
                    .getBoolean("update-checker.console.enabled", true)) {
              return;
            }

            messages.getConfig().getStringList("update-checker.console.text")
                    .forEach(message->getLogger().info(message
                                                               .replace("%currentVersion%",
                                                                        updateCheckerResult.getCurrentVersion())
                                                               .replace("%latestVersion%", updateCheckerResult.getLatestVersion())
                                                      ));
          }
        });
      } catch(final Exception ex) {
        getLogger().warning("Unable to check for updates - check your internet connection: "
                            + ex.getMessage());
      }
    }
  }

  public static PhantomWorlds instance() {

    return instance;
  }

  public static Logger logger() {

    return instance.getLogger();
  }

  public static WorldManager worldManager() {

    return instance.worldManager;
  }

  public static VersionCompatibility compatibility() {

    return compatibility;
  }

  private ArrayList<String> generateCreateSuggestions() {

    final ArrayList<String> suggestions = new ArrayList<>();

    suggestions.addAll(addTrueFalseValues("generatestructures"));
    suggestions.addAll(addTrueFalseValues("genstructures"));
    suggestions.addAll(addTrueFalseValues("structures"));
    suggestions.addAll(addTrueFalseValues("spawnmobs"));
    suggestions.addAll(addTrueFalseValues("mobs"));
    suggestions.addAll(addTrueFalseValues("spawnanimals"));
    suggestions.addAll(addTrueFalseValues("animals"));
    suggestions.addAll(addTrueFalseValues("keepspawninmemory"));
    suggestions.addAll(addTrueFalseValues("spawninmemory"));
    suggestions.addAll(addTrueFalseValues("hardcore"));
    suggestions.addAll(addTrueFalseValues("allowpvp"));
    suggestions.addAll(addTrueFalseValues("pvp"));
    suggestions.addAll(addTrueFalseValues("difficulty"));
    suggestions.addAll(addTrueFalseValues("diff"));

    suggestions.add("generator:");
    suggestions.add("gen:");

    suggestions.add("generatorsettings:");
    suggestions.add("gensettings:");

    suggestions.add("gamemode:ADVENTURE");
    suggestions.add("gamemode:CREATIVE");
    suggestions.add("gamemode:HARDCORE");
    suggestions.add("gamemode:SURVIVAL");

    suggestions.add("seed:");

    for(final WorldType worldType : WorldType.values()) {
      suggestions.add("type:" + worldType.toString());
    }

    return suggestions;
  }

  private ArrayList<String> addTrueFalseValues(String option) {

    final ArrayList<String> list = new ArrayList<>();
    option = option + ":";

    list.add(option + "true");
    list.add(option + "false");

    return list;
  }

  public Map<String, Location> lastLocations() {

    return lastLocations;
  }

  public FoliaLib folia() {

    return folia;
  }
}