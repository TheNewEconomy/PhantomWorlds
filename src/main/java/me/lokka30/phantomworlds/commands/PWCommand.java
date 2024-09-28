package me.lokka30.phantomworlds.commands;
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

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import me.lokka30.phantomworlds.commands.sub.BackupCommand;
import me.lokka30.phantomworlds.commands.sub.CompatibilityCommand;
import me.lokka30.phantomworlds.commands.sub.CopyCommand;
import me.lokka30.phantomworlds.commands.sub.CreateCommand;
import me.lokka30.phantomworlds.commands.sub.DebugCommand;
import me.lokka30.phantomworlds.commands.sub.DeleteCommand;
import me.lokka30.phantomworlds.commands.sub.ImportCommand;
import me.lokka30.phantomworlds.commands.sub.InfoCommand;
import me.lokka30.phantomworlds.commands.sub.ListCommand;
import me.lokka30.phantomworlds.commands.sub.LoadCommand;
import me.lokka30.phantomworlds.commands.sub.ReloadCommand;
import me.lokka30.phantomworlds.commands.sub.SetSpawnCommand;
import me.lokka30.phantomworlds.commands.sub.SpawnCommand;
import me.lokka30.phantomworlds.commands.sub.TeleportCommand;
import me.lokka30.phantomworlds.commands.sub.UnloadCommand;
import me.lokka30.phantomworlds.commands.sub.set.SetEffectsCommand;
import me.lokka30.phantomworlds.commands.sub.set.SetGamemodeCommand;
import me.lokka30.phantomworlds.commands.sub.set.SetPortalCommand;
import me.lokka30.phantomworlds.commands.sub.set.SetTransferCommand;
import me.lokka30.phantomworlds.commands.sub.set.SetWhitelistCommand;
import me.lokka30.phantomworlds.commands.utils.WorldFolder;
import org.bukkit.GameMode;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * PWCommand
 *
 * @author creatorfromhell
 * @since 2.0.5.0
 */
@Command(name = "pw", aliases = { "phantomworlds", "worlds" })
public class PWCommand {

  @Execute(name = "backup", aliases = { "archive", "bu" })
  @Permission("phantomworlds.command.phantomworlds.backup")
  @Description("command.phantomworlds.help.backup")
  public void backup(@Context final CommandSender commandSender, @OptionalArg("world") final World world) {

    BackupCommand.onCommand(commandSender, world);
  }

  @Execute(name = "create", aliases = { "+", "new" })
  @Permission("phantomworlds.command.phantomworlds.create")
  @Description("command.phantomworlds.help.create")
  public void create(@Context final CommandSender commandSender, @Arg("world name") final String name, @Arg("environment") final World.Environment environment, @Arg("world-setting") final List<String> settings) {

    CreateCommand.onCommand(commandSender, name, environment, settings);
  }

  @Execute(name = "copy")
  @Permission("phantomworlds.command.phantomworlds.copy")
  @Description("command.phantomworlds.help.copy")
  public void copy(@Context final CommandSender commandSender, @Arg("world name") final String newWorld, @OptionalArg("world folder") final WorldFolder world) {

    CopyCommand.onCommand(commandSender, newWorld, world);
  }

  @Execute(name = "compatibility")
  @Permission("phantomworlds.command.phantomworlds.compatibility")
  @Description("command.phantomworlds.help.compatibility")
  public void compatibility(@Context final CommandSender commandSender) {

    CompatibilityCommand.onCommand(commandSender);
  }

  @Execute(name = "debug")
  @Permission("phantomworlds.command.phantomworlds.debug")
  @Description("command.phantomworlds.help.debug")
  public void debug(@Context final CommandSender commandSender, @OptionalArg("level") final String level) {

    DebugCommand.onCommand(commandSender, level);
  }

  @Execute(name = "delete", aliases = { "-", "remove", "del" })
  @Permission("phantomworlds.command.phantomworlds.delete")
  @Description("command.phantomworlds.help.delete")
  public void delete(@Context final CommandSender commandSender, @OptionalArg("world") final World world) {

    DeleteCommand.onCommand(commandSender, world);
  }

  @Execute(name = "list", aliases = { "l" })
  @Permission("phantomworlds.command.phantomworlds.list")
  @Description("command.phantomworlds.help.list")
  public void list(@Context final CommandSender commandSender) {

    ListCommand.onCommand(commandSender);
  }

  @Execute(name = "import", aliases = { "im" })
  @Permission("phantomworlds.command.phantomworlds.import")
  @Description("command.phantomworlds.help.import")
  public void importCMD(@Context final CommandSender commandSender, @OptionalArg("world") final World world) {

    ImportCommand.onCommand(commandSender, world);
  }

  @Execute(name = "info", aliases = { "i" })
  @Permission("phantomworlds.command.phantomworlds.info")
  @Description("command.phantomworlds.help.info")
  public void info(@Context final CommandSender commandSender) {

    InfoCommand.onCommand(commandSender);
  }

  @Execute(name = "load")
  @Permission("phantomworlds.command.phantomworlds.load")
  @Description("command.phantomworlds.help.load")
  public void load(@Context final CommandSender commandSender, @OptionalArg("world folder") final WorldFolder world) {

    LoadCommand.onCommand(commandSender, world);
  }

  @Execute(name = "reload", aliases = { "r" })
  @Permission("phantomworlds.command.phantomworlds.reload")
  @Description("command.phantomworlds.help.reload")
  public void reload(@Context final CommandSender commandSender) {

    ReloadCommand.onCommand(commandSender);
  }

  @Execute(name = "set effects", aliases = { "set eff" })
  @Permission("phantomworlds.command.phantomworlds.set.effects")
  @Description("command.phantomworlds.help.seteffects")
  public void setEffects(@Context final CommandSender commandSender, @Arg("world") final World world, @Arg("potion-effects") final List<String> effects) {

    SetEffectsCommand.onCommand(commandSender, world, effects);
  }

  @Execute(name = "set gamemode", aliases = { "set mode" })
  @Permission("phantomworlds.command.phantomworlds.set.gamemode")
  @Description("command.phantomworlds.help.setgamemode")
  public void setGamemode(@Context final CommandSender commandSender, @Arg("world") final World world, @Arg("mode") final GameMode mode) {

    SetGamemodeCommand.onCommand(commandSender, world, mode);
  }

  @Execute(name = "set portal")
  @Permission("phantomworlds.command.phantomworlds.set.portal")
  @Description("command.phantomworlds.help.setportal")
  public void setPortal(@Context final CommandSender commandSender, @Arg("world") final World world, @Arg("portal type") final PortalType portal, @Arg("world to") final World worldTo) {

    SetPortalCommand.onCommand(commandSender, world, portal, worldTo);
  }

  @Execute(name = "set transfer")
  @Permission("phantomworlds.command.phantomworlds.set.transfer")
  @Description("command.phantomworlds.help.settransfer")
  public void setPortal(@Context final CommandSender commandSender, @Arg("world") final World world, @Arg("portal type") final PortalType portal, @Arg("ip:port") final String ip) {

    SetTransferCommand.onCommand(commandSender, world, portal, ip);
  }

  @Execute(name = "set whitelist")
  @Permission("phantomworlds.command.phantomworlds.set.whitelist")
  @Description("command.phantomworlds.help.setwhitelist")
  public void setWhitelist(@Context final CommandSender commandSender, @Arg("world") final World world, @Arg("whitelist") final boolean whitelist) {

    SetWhitelistCommand.onCommand(commandSender, world, whitelist);
  }

  @Execute(name = "setspawn", aliases = { "ss" })
  @Permission("phantomworlds.command.phantomworlds.setspawn")
  @Description("command.phantomworlds.help.setspawn")
  public void setspawn(@Context final CommandSender commandSender, @OptionalArg("x") final Double x, @OptionalArg("y") final Double y, @OptionalArg("z") final Double z, @OptionalArg("world") final World world, @OptionalArg("yaw") final Float yaw, @OptionalArg("pitch") final Float pitch) {

    SetSpawnCommand.onCommand(commandSender, x, y, z, world, yaw, pitch);
  }

  @Execute(name = "spawn")
  @Permission("phantomworlds.command.phantomworlds.spawn")
  @Description("command.phantomworlds.help.spawn")
  public void spawn(@Context final CommandSender commandSender, @OptionalArg("world") final World world, @OptionalArg("target") final Player player) {

    SpawnCommand.onCommand(commandSender, world, player);
  }

  @Execute(name = "teleport", aliases = { "tp" })
  @Permission("phantomworlds.command.phantomworlds.teleport")
  @Description("command.phantomworlds.help.tp")
  public void tp(@Context final CommandSender commandSender, @OptionalArg("world") final World world, @OptionalArg("target") final Player player) {

    TeleportCommand.onCommand(commandSender, world, player);
  }

  @Execute(name = "unload", aliases = { "u" })
  @Permission("phantomworlds.command.phantomworlds.unload")
  @Description("command.phantomworlds.help.unload")
  public void unload(@Context final CommandSender commandSender, @OptionalArg("world") final World world) {

    UnloadCommand.onCommand(commandSender, world);
  }
}