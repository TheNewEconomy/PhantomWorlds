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
import org.bukkit.command.CommandSender;

/**
 * SettingParameterRedux
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class SettingParameter extends ArgumentResolver<CommandSender, String> {

  @Override
  protected ParseResult<String> parse(final Invocation<CommandSender> invocation, final Argument<String> context, final String argument) {

    if(argument.isEmpty()) {
      return ParseResult.failure("Invalid effect argument.");
    }
    return ParseResult.success(argument);
  }

  @Override
  public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<String> argument, final SuggestionContext context) {

    return SuggestionResult.of(PhantomWorlds.createTabs);
  }
}