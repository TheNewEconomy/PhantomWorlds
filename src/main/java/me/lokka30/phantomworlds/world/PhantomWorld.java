package me.lokka30.phantomworlds.world;
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
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * PhantomWorld object to make it easier to work with PW-managed worlds.
 *
 * @author lokka30
 * @since v2.0.0
 */
public class PhantomWorld {

  private final String name;
  private final World.Environment environment;
  private final boolean generateStructures;
  private final String generator;
  private final String generatorSettings;
  private final boolean hardcore;
  private final Long seed;
  private final WorldType worldType;
  private final boolean spawnMobs;
  private final boolean spawnAnimals;
  private final boolean keepSpawnInMemory;
  private final boolean allowPvP;
  private final Difficulty difficulty;

  private final GameMode gameMode;

  public PhantomWorld(
          @NotNull final String name,
          @NotNull final World.Environment environment,
          final boolean generateStructures,
          @Nullable final String generator,
          @Nullable final String generatorSettings,
          final boolean hardcore,
          @Nullable final Long seed,
          @NotNull final WorldType worldType,
          final boolean spawnMobs,
          final boolean spawnAnimals,
          final boolean keepSpawnInMemory,
          final boolean allowPvP,
          @NotNull final Difficulty difficulty,
          @NotNull final GameMode gameMode
                     ) {

    this.name = name;
    this.environment = environment;
    this.generateStructures = generateStructures;
    this.generator = generator;
    this.generatorSettings = generatorSettings;
    this.hardcore = hardcore;
    this.seed = seed;
    this.worldType = worldType;
    this.spawnMobs = spawnMobs;
    this.spawnAnimals = spawnAnimals;
    this.keepSpawnInMemory = keepSpawnInMemory;
    this.allowPvP = allowPvP;
    this.difficulty = difficulty;
    this.gameMode = gameMode;
  }

  /**
   * Create/import the world with specified settings.
   *
   * @since v2.0.0
   */
  public void create() {

    final WorldCreator worldCreator = new WorldCreator(name);

    if(environment.equals(World.Environment.CUSTOM)) {

      worldCreator.environment(World.Environment.NORMAL);
    } else {

      worldCreator.environment(environment);
    }
    worldCreator.generateStructures(generateStructures);
    try {
      worldCreator.hardcore(hardcore);
    } catch(final NoSuchMethodError ignored) {
    }
    worldCreator.type(worldType);

    if(generator != null) {
      worldCreator.generator(generator);
    }
    if(generatorSettings != null) {
      worldCreator.generatorSettings(generatorSettings);
    }
    if(seed != null) {
      worldCreator.seed(seed);
    }

    final World world = worldCreator.createWorld();

    if(world == null) {
      PhantomWorlds.logger().severe("Unable to create/load world '" + name + "'!");
      return;
    }

    world.setSpawnFlags(spawnMobs, spawnAnimals);
    world.setKeepSpawnInMemory(keepSpawnInMemory);
    world.setPVP(allowPvP);
    world.setDifficulty(difficulty);
  }

  public void save() {

    final String cfgPath = "worlds-to-load." + name + ".";
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "environment", environment.toString());
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "generateStructures", generateStructures);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "generator", generator);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "generatorSettings", generatorSettings);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "hardcore", hardcore);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "seed", seed);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "worldType", worldType.toString());
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "spawnMobs", spawnMobs);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "spawnAnimals", spawnAnimals);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "keepSpawnInMemory", keepSpawnInMemory);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "allowPvP", allowPvP);
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "difficulty", difficulty.toString());
    PhantomWorlds.instance().data.getConfig().set(cfgPath + "gameMode", gameMode.name());

    try {
      PhantomWorlds.instance().data.save();
    } catch(final IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public String name() {

    return name;
  }

  public World.Environment environment() {

    return environment;
  }

  public boolean generateStructures() {

    return generateStructures;
  }

  public String generator() {

    return generator;
  }

  public String generatorSettings() {

    return generatorSettings;
  }

  public boolean hardcore() {

    return hardcore;
  }

  public Long seed() {

    return seed;
  }

  public WorldType worldType() {

    return worldType;
  }

  public boolean spawnMobs() {

    return spawnMobs;
  }

  public boolean spawnAnimals() {

    return spawnAnimals;
  }

  public boolean keepSpawnInMemory() {

    return keepSpawnInMemory;
  }

  public boolean allowPvP() {

    return allowPvP;
  }

  public Difficulty difficulty() {

    return difficulty;
  }
}