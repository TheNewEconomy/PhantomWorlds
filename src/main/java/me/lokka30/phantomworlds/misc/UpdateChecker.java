package me.lokka30.phantomworlds.misc;
/*
 * Phantom Worlds
 * Copyright (C) 2023 - 2024 Daniel "creatorfromhell" Vidmar
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

import me.lokka30.phantomworlds.PhantomWorlds;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * UpdateChecker
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class UpdateChecker {
  private final JavaPlugin plugin;
  private final int resourceId;

  public UpdateChecker(final JavaPlugin plugin, final int resourceId) {
    this.plugin = plugin;
    this.resourceId = resourceId;
  }

  public void getLatestVersion(final Consumer<String> consumer) {

    PhantomWorlds.instance().folia().getScheduler().runAsync((task) -> {
      final InputStream inputStream;
      try {
        inputStream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId)).openStream();
      } catch (final IOException ignore) {
        return;
      }

      final Scanner scanner = new Scanner(inputStream);
      if (scanner.hasNext()) {
        consumer.accept(scanner.next());
      }

    });
  }

  public String getCurrentVersion() {
    return this.plugin.getDescription().getVersion();
  }
}