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

import me.lokka30.microlib.messaging.MultiMessage;
import me.lokka30.phantomworlds.PhantomWorlds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Arrays;

/**
 * PlayerTeleportEvent
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
public class PlayerTeleportListener implements Listener {

  final PhantomWorlds plugin;

  public PlayerTeleportListener(final PhantomWorlds plugin) {

    this.plugin = plugin;
  }

  @EventHandler
  public void onTP(final PlayerTeleportEvent event) {

    if(event.getTo() == null || event.getTo().getWorld() == null || event.getFrom().getWorld() == null) {
      return;
    }

    if(event.getFrom().getWorld().getUID().equals(event.getTo().getWorld().getUID())) {
      return;
    }

    final String cfgPath = "worlds-to-load." + event.getTo().getWorld().getName();

    if(!event.getPlayer().isOp() && PhantomWorlds.instance().data.getConfig().getBoolean(cfgPath + ".whitelist", false)
       && !event.getPlayer().hasPermission("phantomworlds.world.access." + event.getTo().getWorld().getName())) {
      event.setCancelled(true);
    }

    /*if(!event.getPlayer().isOp() && event.getPlayer().hasPermission("phantomworlds.world.deny." + event.getTo().getWorld().getName())) {
      event.setCancelled(true);
    }*/

    if(plugin.worldManager.tpAwaiting.containsKey(event.getPlayer().getUniqueId())) {
      if(event.isCancelled()) {

        (new MultiMessage(
                PhantomWorlds.instance().messages.getConfig()
                        .getStringList("common.denied"),
                Arrays.asList(
                        new MultiMessage.Placeholder("prefix", PhantomWorlds.instance().messages.getConfig()
                                .getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                        new MultiMessage.Placeholder("world", event.getTo().getWorld().getName(), false)
                             ))).send(event.getPlayer());

      } else {
        plugin.worldManager.tpAwaiting.get(event.getPlayer().getUniqueId()).send(event.getPlayer());
      }

      plugin.worldManager.tpAwaiting.remove(event.getPlayer().getUniqueId());
    }

    PhantomWorlds.instance().lastLocations().put(event.getPlayer().getUniqueId() + "-" + event.getFrom().getWorld().getName(), event.getFrom());
  }
}