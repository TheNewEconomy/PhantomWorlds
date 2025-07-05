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
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * PlayerChangeWorldListener
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class PlayerChangeWorldListener implements Listener {

  final PhantomWorlds plugin;

  public PlayerChangeWorldListener(final PhantomWorlds plugin) {

    this.plugin = plugin;
  }

  @EventHandler
  public void onChangeWorld(final PlayerChangedWorldEvent event) {

    //Check if this world has a PhantomWorlds managed spawn. If so, teleport the player there.

    final String cfgPath = "worlds-to-load." + event.getPlayer().getWorld().getName();
    final String spawnPath = cfgPath + ".spawn";

    if(PhantomWorlds.instance().settings.getConfig().getBoolean("spawning.change", false) && PhantomWorlds.instance().data.getConfig().contains(spawnPath)) {

      final double x = PhantomWorlds.instance().data.getConfig().getDouble(spawnPath + ".x", event.getPlayer().getWorld().getSpawnLocation().getX());
      final double y = PhantomWorlds.instance().data.getConfig().getDouble(spawnPath + ".y", event.getPlayer().getWorld().getSpawnLocation().getY());
      final double z = PhantomWorlds.instance().data.getConfig().getDouble(spawnPath + ".z", event.getPlayer().getWorld().getSpawnLocation().getZ());
      final float yaw = (float)PhantomWorlds.instance().data.getConfig().getDouble(spawnPath + ".yaw", event.getPlayer().getWorld().getSpawnLocation().getYaw());
      final float pitch = (float)PhantomWorlds.instance().data.getConfig().getDouble(spawnPath + ".pitch", event.getPlayer().getWorld().getSpawnLocation().getPitch());

      event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), x, y, z, yaw, pitch));
    }

    final String cfgPrevPath = "worlds-to-load." + event.getFrom().getName();
    if(PhantomWorlds.instance().data.getConfig().contains(cfgPrevPath + ".effects") &&
       PhantomWorlds.instance().data.getConfig().isConfigurationSection(cfgPrevPath + ".effects")) {
      for(final String effName : PhantomWorlds.instance().data.getConfig().getConfigurationSection(cfgPrevPath + ".effects").getKeys(false)) {

        final PotionEffectType type = PhantomWorlds.compatibility().findType(effName);
        if(type != null) {
          event.getPlayer().removePotionEffect(type);
        }
      }
    }

    Utils.applyWorldEffects(event.getPlayer(), event.getPlayer().getWorld().getName());
  }
}