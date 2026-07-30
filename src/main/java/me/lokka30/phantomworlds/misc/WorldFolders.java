package me.lokka30.phantomworlds.misc;
/*
 * Phantom Worlds
 * Copyright (C) 2026 Daniel "creatorfromhell" Vidmar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.vdurmont.semver4j.Semver;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * WorldFolders
 *
 * @author creatorfromhell
 * @since 2.2.0
 */
public final class WorldFolders {

  private static final Semver PAPER_26_1 = new Semver("26.1.0", Semver.SemverType.LOOSE);
  private static final Pattern DIGITS = Pattern.compile("\\d+");

  private WorldFolders() {
  }

  public static boolean usesModernLayout() {
    final Semver current = new Semver(version(), Semver.SemverType.LOOSE
    );

    return current.isGreaterThanOrEqualTo(PAPER_26_1);
  }

  /**
   * Resolves a world folder by name.
   *
   * Loaded worlds are resolved through Bukkit, which should always be preferred.
   * Unloaded worlds are located by scanning the filesystem.
   */
  @Nullable
  public static File find(final String worldName) {
    final World loadedWorld = Bukkit.getWorld(worldName);

    if(loadedWorld != null) {
      return loadedWorld.getWorldFolder();
    }

    return availableWorldFolders().get(worldName);
  }

  /**
   * Resolves a folder for a world that may not exist yet.
   *
   * This should be used for creating or copying a named plugin world.
   */
  public static File resolveForCreation(final String worldName) {
    final File existing = find(worldName);

    if(existing != null) {
      return existing;
    }

    return new File(Bukkit.getWorldContainer(), worldName);
  }

  /**
   * Returns discoverable world folders keyed by the Bukkit-compatible name
   * currently used by PhantomWorlds.
   */
  public static Map<String, File> availableWorldFolders() {
    final Map<String, File> worlds = new LinkedHashMap<>();

    // Loaded worlds are authoritative.
    for(final World world : Bukkit.getWorlds()) {
      worlds.put(world.getName(), world.getWorldFolder());
    }

    if(usesModernLayout()) {
      discoverModernWorlds(worlds);
    } else {
      discoverLegacyWorlds(worlds);
    }

    return worlds;
  }

  public static Collection<String> availableWorldNames() {
    return availableWorldFolders().keySet();
  }

  private static void discoverLegacyWorlds(final Map<String, File> worlds) {
    final File[] entries = Bukkit.getWorldContainer().listFiles(File::isDirectory);

    if(entries == null) {
      return;
    }

    for(final File entry : entries) {
      if(isLevelRoot(entry)) {
        worlds.putIfAbsent(entry.getName(), entry);
      }
    }
  }

  private static void discoverModernWorlds(final Map<String, File> worlds) {
    final File container = Bukkit.getWorldContainer();
    final File[] levelRoots = container.listFiles(File::isDirectory);

    if(levelRoots == null) {
      return;
    }

    for(final File levelRoot : levelRoots) {
      if(!isLevelRoot(levelRoot)) {
        continue;
      }

      final String levelName = levelRoot.getName();

      // Fallback for layouts where the root itself represents the overworld.
      worlds.putIfAbsent(levelName, levelRoot);

      final File dimensionsDirectory = new File(levelRoot, "dimensions");
      final File[] namespaces = dimensionsDirectory.listFiles(File::isDirectory);

      if(namespaces == null) {
        continue;
      }

      for(final File namespace : namespaces) {
        final File[] dimensions = namespace.listFiles(File::isDirectory);

        if(dimensions == null) {
          continue;
        }

        for(final File dimension : dimensions) {
          if(!isDimensionFolder(dimension)) {
            continue;
          }

          final String worldName = modernWorldName(
                  levelName,
                  namespace.getName(),
                  dimension.getName()
                                                  );

          worlds.putIfAbsent(worldName, dimension);
        }
      }
    }
  }

  private static String modernWorldName(
          final String levelName,
          final String namespace,
          final String dimension
                                       ) {
    if(namespace.equals("minecraft")) {
      switch(dimension) {
        case "overworld":
          return levelName;

        case "the_nether":
          return levelName + "_nether";

        case "the_end":
          return levelName + "_the_end";

        default:
          return dimension;
      }
    }

    return dimension;
  }

  private static boolean isLevelRoot(final File directory) {
    return directory.isDirectory()
           && new File(directory, "level.dat").isFile();
  }

  private static boolean isDimensionFolder(final File directory) {
    if(!directory.isDirectory()) {
      return false;
    }

    return new File(directory, "region").isDirectory()
           || new File(directory, "entities").isDirectory()
           || new File(directory, "poi").isDirectory();
  }

  private static void scanRecursively(final File directory, final Map<String, File> worlds, final int depth) {

    if(depth < 0 || !directory.isDirectory()) {
      return;
    }

    if(isLevelFolder(directory)) {
      worlds.putIfAbsent(directory.getName(), directory);
    }

    final File[] children = directory.listFiles(File::isDirectory);

    if(children == null) {
      return;
    }

    for(final File child : children) {
      if(shouldIgnore(child)) {
        continue;
      }

      scanRecursively(child, worlds, depth - 1);
    }
  }

  private static boolean isLevelFolder(final File directory) {
    return directory.isDirectory() && new File(directory, "level.dat").isFile();
  }

  private static boolean shouldIgnore(final File directory) {
    final String name = directory.getName();

    return name.equals("plugins") || name.equals("logs") || name.equals("cache") || name.equals("libraries") || name.equals("versions") || name.equals(".git");
  }

  private static String version() {

    String version = Bukkit.getServer().getBukkitVersion();
    if (version.indexOf('-') != -1)
      version = version.substring(0, version.indexOf('-'));

    final String[] split = version.split("\\.");
    if(split.length == 1) {
      return split[0] + ".0.0";
    }

    if (split.length == 2) {
      return split[0] + "." + split[1] + ".0";
    }

    String minorSlice = split[2];
    if (minorSlice.indexOf('-') != -1)
      minorSlice = minorSlice.substring(0, minorSlice.indexOf('-'));

    int patch = 0;
    if (DIGITS.matcher(minorSlice).matches()) {
      patch = Integer.parseInt(minorSlice);
    }
    return split[0] + "." + split[1] + "." + patch;
  }
}