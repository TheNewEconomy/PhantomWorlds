package me.lokka30.phantomworlds.commands.sub;
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

import me.lokka30.microlib.messaging.MultiMessage;
import me.lokka30.phantomworlds.PhantomWorlds;
import me.lokka30.phantomworlds.misc.WorldFolders;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * ListCommand
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class ListCommand {

  public static void onCommand(final CommandSender sender) {

    (new MultiMessage(
            PhantomWorlds.instance().messages.getConfig()
                    .getStringList("command.phantomworlds.subcommands.list.header-loaded"), Arrays.asList(
            new MultiMessage.Placeholder("prefix",
                                         PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                         true),
            new MultiMessage.Placeholder("amount", String.valueOf(Bukkit.getWorlds().size()), false)
                                                                                                         ))).send(sender);

    final HashSet<String> loaded = new HashSet<>();

    for(final World world : Bukkit.getWorlds()) {
      loaded.add(world.getName());
    }

    for(final String world : loaded) {
      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.subcommands.list.entry"), Arrays.asList(
              new MultiMessage.Placeholder("prefix",
                                           PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                           true),
              new MultiMessage.Placeholder("world", world, false)
                                                                                                   ))).send(sender);
    }

    final HashSet<String> unloaded = new HashSet<>();

    for(final String worldName : WorldFolders.availableWorldNames()) {
      if(!loaded.contains(worldName)) {
        unloaded.add(worldName);
      }
    }

    (new MultiMessage(
            PhantomWorlds.instance().messages.getConfig()
                    .getStringList("command.phantomworlds.subcommands.list.header-unloaded"), Arrays.asList(
            new MultiMessage.Placeholder("prefix",
                                         PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                         true),
            new MultiMessage.Placeholder("amount", String.valueOf(unloaded.size()), false)
                                                                                                           ))).send(sender);

    for(final String world : unloaded) {
      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.subcommands.list.entry"), Arrays.asList(
              new MultiMessage.Placeholder("prefix",
                                           PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                           true),
              new MultiMessage.Placeholder("world", world, false)
                                                                                                   ))).send(sender);
    }

    final HashSet<String> archived = new HashSet<>();
    final File dir = new File(PhantomWorlds.instance().getDataFolder(), PhantomWorlds.BACKUP_FOLDER);

    //TODO: archived, last backup times.
    final File[] archivedFolders = dir.listFiles(File::isDirectory);
    if(archivedFolders != null) {
      for(final File file : archivedFolders) {
        if(!loaded.contains(file.getName())
           && !unloaded.contains(file.getName())) {
          archived.add(file.getName());
        }
      }
    }

    (new MultiMessage(
            PhantomWorlds.instance().messages.getConfig()
                    .getStringList("command.phantomworlds.subcommands.list.header-archived"), Arrays.asList(
            new MultiMessage.Placeholder("prefix",
                                         PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                         true),
            new MultiMessage.Placeholder("amount", String.valueOf(archived.size()), false)
                                                                                                           ))).send(sender);

    for(final String world : archived) {
      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.subcommands.list.entry"), Arrays.asList(
              new MultiMessage.Placeholder("prefix",
                                           PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                           true),
              new MultiMessage.Placeholder("world", world, false)
                                                                                                   ))).send(sender);
    }
  }
}