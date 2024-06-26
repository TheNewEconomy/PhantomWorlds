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

/**
 * WorldLoadResponse
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public enum WorldLoadResponse {

  /**
   * Success: World load response.
   */
  LOADED,

  /**
   * Failure: World not found.
   */
  NOT_FOUND,

  /**
   * Failure: Already loaded.
   */
  ALREADY_LOADED,

  /**
   * Failure: Invalid world.
   */
  INVALID,

  /**
   * Failure: Marked as skipped in data.yml
   */
  CONFIG_SKIPPED
}