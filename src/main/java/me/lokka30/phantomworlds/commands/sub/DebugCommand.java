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

import me.lokka30.microlib.messaging.MessageUtils;
import org.bukkit.command.CommandSender;

import java.util.Locale;

/**
 * DebugCommand
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class DebugCommand {

  public static void onCommand(final CommandSender sender, final String level) {

    final String parsed = (level == null)? "nothing" : level;
    switch(parsed.toLowerCase(Locale.ROOT)) {
      case "dump":
        sender.sendMessage(
                MessageUtils.colorizeStandardCodes("&b&lPhantomWorlds: &7Incomplete."));
        break;
      default:
        sender.sendMessage(MessageUtils.colorizeStandardCodes(
                                           "&b&lPhantomWorlds: &7Invalid debug method '%method%'.")
                                   .replace("%method%", parsed)
                          );

        sender.sendMessage(MessageUtils.colorizeStandardCodes(
                "&b&lPhantomWorlds: &7Note: Please do not run this subcommand unless you are sure you are meant to be doing so."));
        break;
    }
  }
}