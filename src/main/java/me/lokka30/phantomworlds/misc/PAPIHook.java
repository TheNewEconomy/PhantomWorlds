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

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PAPIHook
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class PAPIHook extends PlaceholderExpansion {

  @Override
  public @NotNull String getIdentifier() {

    return "pworlds";
  }

  @Override
  public @NotNull String getAuthor() {

    return "creatorfromhell";
  }

  @Override
  public @NotNull String getVersion() {

    return "2.0.8";
  }

  @Override
  public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {

    if(player == null) {
      return null;
    }

    final String[] args = params.split("_");

    //%pworlds_name%
    if(args[0].equalsIgnoreCase("name")) {

      return player.getWorld().getName();
    }

    //%pworlds_time%
    if(args[0].equalsIgnoreCase("time")) {

      return String.valueOf(player.getWorld().getTime());
    }

    //%pworlds_period%
    if(args[0].equalsIgnoreCase("period")) {
      return period(player.getWorld().getTime());
    }

    //%pworlds_environment%
    if(args[0].equalsIgnoreCase("environment")) {
      return player.getWorld().getEnvironment().name();
    }

    return null;
  }

  private String period(final long time) {

    if(time >= 6000 && time <= 11999) {
      return "noon";
    } else if(time >= 12000 && time <= 12999) {
      return "sunset";
    } else if(time >= 13000 && time <= 17999) {
      return "night";
    } else if(time >= 18000 && time <= 22999) {
      return "midnight";
    } else if(time >= 23000 || time <= 999) {
      return "sunrise";
    }
    return "day";
  }
}