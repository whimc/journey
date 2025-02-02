/*
 * MIT License
 *
 * Copyright 2021 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package edu.whimc.journey.spigot.command.to;

import edu.whimc.journey.common.data.DataAccessException;
import edu.whimc.journey.common.data.PersonalEndpointManager;
import edu.whimc.journey.common.tools.BufferedFunction;
import edu.whimc.journey.common.util.Validator;
import edu.whimc.journey.spigot.JourneySpigot;
import edu.whimc.journey.spigot.command.JourneyCommand;
import edu.whimc.journey.spigot.command.common.CommandError;
import edu.whimc.journey.spigot.command.common.CommandNode;
import edu.whimc.journey.spigot.command.common.Parameter;
import edu.whimc.journey.spigot.command.common.PlayerCommandNode;
import edu.whimc.journey.spigot.navigation.LocationCell;
import edu.whimc.journey.spigot.util.Format;
import edu.whimc.journey.spigot.util.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A command that allows the player to journey to a personal destination.
 */
public class JourneyToMyCommand extends PlayerCommandNode {

  /**
   * General constructor.
   *
   * @param parent the parent
   */
  public JourneyToMyCommand(@NotNull CommandNode parent) {
    super(parent,
        Permissions.JOURNEY_TO_CUSTOM_USE,
        "Blaze a trail to a custom destination",
        "my");

    BufferedFunction<Player, List<String>> customLocationsFunction =
        JourneyCommand.bufferedPersonalEndpointFunction();
    addSubcommand(Parameter.builder()
        .supplier(Parameter.ParameterSupplier.builder()
            .usage("<name>")
            .allowedEntries((src, prev) -> {
              if (src instanceof Player) {
                return customLocationsFunction.apply((Player) src);
              } else {
                return new ArrayList<>();
              }
            }).build())
        .build(), "Use a previously saved custom location");

  }

  @Override
  public boolean onWrappedPlayerCommand(@NotNull Player player,
                                        @NotNull Command command,
                                        @NotNull String label,
                                        @NotNull String[] args,
                                        @NotNull Map<String, String> flags) throws DataAccessException {

    LocationCell endLocation;

    if (args.length == 0) {
      sendCommandUsageError(player, CommandError.FEW_ARGUMENTS);
      return false;
    }

    PersonalEndpointManager<LocationCell, World> personalEndpointManager = JourneySpigot.getInstance()
        .getDataManager()
        .getPersonalEndpointManager();
    try {
      endLocation = personalEndpointManager.getPersonalEndpoint(player.getUniqueId(), args[0]);

      if (endLocation == null) {
        player.spigot().sendMessage(Format.error("The custom location ",
            Format.toPlain(Format.note(args[0])),
            " could not be found."));
        return false;
      }
    } catch (IllegalArgumentException e) {
      player.spigot().sendMessage(Format.error("Your numbers could not be read."));
      return false;
    }

    JourneyCommand.journeyTo(player, endLocation, flags);

    // Check if we should save a custom endpoint
    if (args.length >= 5) {
      if (personalEndpointManager.hasPersonalEndpoint(player.getUniqueId(), endLocation)) {
        player.spigot().sendMessage(Format.error("A custom location already exists at that location!"));
        return false;
      }
      if (personalEndpointManager.hasPersonalEndpoint(player.getUniqueId(), args[4])) {
        player.spigot().sendMessage(Format.error("A custom location already exists with that name!"));
        return false;
      }
      if (Validator.isInvalidDataName(args[5])) {
        player.spigot().sendMessage(Format.error("Your custom name ",
            Format.toPlain(Format.note(args[4])),
            " contains illegal characters."));
        return false;
      }
      // Save it!
      personalEndpointManager.addPersonalEndpoint(player.getUniqueId(), endLocation, args[4]);
      player.spigot().sendMessage(Format.success("Saved your custom location with name ",
          Format.toPlain(Format.note(args[4])), "!"));
    }

    return true;

  }
}