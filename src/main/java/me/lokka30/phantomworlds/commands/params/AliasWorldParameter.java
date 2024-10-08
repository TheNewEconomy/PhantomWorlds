package me.lokka30.phantomworlds.commands.params;
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

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import me.lokka30.phantomworlds.PhantomWorlds;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * AliasWorldParameter
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class AliasWorldParameter extends ArgumentResolver<CommandSender, World> {

  @Override
  protected ParseResult<World> parse(final Invocation<CommandSender> invocation, final Argument<World> context, final String argument) {

    final World world = PhantomWorlds.worldManager().findWorld(argument);
    if(world == null) {
      return ParseResult.failure("Invalid world name");
    }

    return ParseResult.success(world);
  }

  @Override
  public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<World> argument, final SuggestionContext context) {

    final List<String> worlds = new ArrayList<>(PhantomWorlds.worldManager().aliases.keySet());

    for(final World world : Bukkit.getWorlds()) {
      worlds.add(world.getName());
    }
    return SuggestionResult.of(worlds);
  }
}