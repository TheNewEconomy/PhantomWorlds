package me.lokka30.phantomworlds.listeners.player;
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
import me.lokka30.phantomworlds.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * PlayerLoginListener
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class PlayerJoinListener implements Listener {

  final PhantomWorlds plugin;

  public PlayerJoinListener(final PhantomWorlds plugin) {

    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {

    final String spawnWorld = PhantomWorlds.instance().settings.getConfig().getString("spawning.default-world", "world");
    final boolean spawnAlways = PhantomWorlds.instance().settings.getConfig().getBoolean("spawning.respawn-always", false);
    final World sWorld = Bukkit.getWorld(spawnWorld);
    if(sWorld == null) {

      Utils.applyWorldEffects(event.getPlayer(), event.getPlayer().getWorld().getName());
      plugin.getLogger().warning("Configured spawn world doesn't exist! Not changing player spawn location.");
      return;
    }

    //We don't manage so send the player to the spawn world
    if(spawnAlways || !event.getPlayer().hasPlayedBefore() && PhantomWorlds.instance().settings.getConfig().getBoolean("spawning.default-first", true)) {

      Utils.teleport(event.getPlayer(), Utils.parseSpawn(sWorld));
    }

    Utils.applyWorldEffects(event.getPlayer(), sWorld.getName());
  }
}