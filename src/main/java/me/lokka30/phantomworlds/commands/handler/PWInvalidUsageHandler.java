package me.lokka30.phantomworlds.commands.handler;
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

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * InvalidUsageHandler
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class PWInvalidUsageHandler implements InvalidUsageHandler<CommandSender> {

  @Override
  public void handle(final Invocation<CommandSender> invocation, final InvalidUsage<CommandSender> result, final ResultHandlerChain<CommandSender> chain) {

    final CommandSender sender = invocation.sender();
    final Schematic schematic = result.getSchematic();

    if(schematic.isOnlyFirst()) {
      sender.sendMessage(color("&cInvalid usage of command! &7(" + schematic.first() + ")"));
      return;
    }

    sender.sendMessage(color("&cInvalid usage of command!"));
    for(final String scheme : schematic.all()) {
      sender.sendMessage(color("&8 - &7" + scheme));
    }
  }

  private String color(final String message) {

    return ChatColor.translateAlternateColorCodes('&', message);
  }
}
