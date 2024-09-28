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
import me.lokka30.phantomworlds.commands.utils.WorldFolder;
import me.lokka30.phantomworlds.misc.WorldCopyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

import static me.lokka30.phantomworlds.misc.WorldCopyResponse.COPIED;
import static me.lokka30.phantomworlds.misc.WorldCopyResponse.INVALID;

/**
 * LoadCommand
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class CopyCommand {

  public static void onCommand(final CommandSender sender, final String newWorld, final WorldFolder world) {

    if(world == null || world.getFolder() == null || newWorld.isEmpty()) {
      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.usages.copy"), Arrays.asList(
              new MultiMessage.Placeholder("prefix",
                                           PhantomWorlds.instance().messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"),
                                           true),
              new MultiMessage.Placeholder("label", "pw", false)
                                                                                        ))).send(sender);
      return;
    }

    if(Bukkit.getWorld(newWorld) != null) {
      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.subcommands.create.already-loaded"),
              Arrays.asList(
                      new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                              .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                      new MultiMessage.Placeholder("world", newWorld, false),
                      new MultiMessage.Placeholder("label", "pw", false)
                           ))).send(sender);
      return;
    }

    final WorldCopyResponse response = PhantomWorlds.worldManager().copyWorld(world.getFolder(), newWorld);

    if(response == INVALID) {
      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.subcommands.copy.failure-copying"),
              Arrays.asList(
                      new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                              .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                      new MultiMessage.Placeholder("world", world.getFolder(), false),
                      new MultiMessage.Placeholder("label", "pw", false)
                           ))).send(sender);
      return;
    }

    if(response != COPIED) {

      (new MultiMessage(
              PhantomWorlds.instance().messages.getConfig()
                      .getStringList("command.phantomworlds.subcommands.copy.failure-loading"),
              Arrays.asList(
                      new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                              .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                      new MultiMessage.Placeholder("world", world.getFolder(), false),
                      new MultiMessage.Placeholder("label", "pw", false)
                           ))).send(sender);
      return;
    }

    (new MultiMessage(
            PhantomWorlds.instance().messages.getConfig().getStringList(
                    "command.phantomworlds.subcommands.copy.success"),
            Arrays.asList(
                    new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                            .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                    new MultiMessage.Placeholder("world", world.getFolder(), false),
                    new MultiMessage.Placeholder("new_world", newWorld, false)
                         ))).send(sender);
  }
}