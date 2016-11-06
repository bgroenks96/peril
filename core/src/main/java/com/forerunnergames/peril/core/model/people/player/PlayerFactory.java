/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

public final class PlayerFactory
{
  private final ImmutableSet.Builder <Player> playerSetBuilder = ImmutableSet.builder ();
  private int playerCount = 0;

  static Player createHuman (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return create (name, PersonSentience.HUMAN);
  }

  static Player create (final String name, final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (sentience, "sentience");

    return new PlayerBuilder (name, sentience).build ();
  }

  static Player createHuman (final String name, final PlayerColor color, final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return builder (name, PersonSentience.HUMAN).color (color).turnOrder (turnOrder).build ();
  }

  static PlayerFactory from (final ImmutableCollection <Player> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    final PlayerFactory factory = new PlayerFactory ();
    for (final Player player : players)
    {
      factory.playerSetBuilder.add (player);
      factory.playerCount++;
    }
    return factory;
  }

  public static PlayerBuilder builder (final String playerName, final PersonSentience playerSentience)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return new PlayerBuilder (playerName, playerSentience);
  }

  public void newHumanPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    playerSetBuilder.add (createHuman (name));
    playerCount++;
  }

  public void newPlayerWith (final String name, final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (sentience, "sentience");

    playerSetBuilder.add (create (name, sentience));
    playerCount++;
  }

  public void newHumanPlayerWith (final String name, final PlayerColor color, final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    playerSetBuilder.add (createHuman (name, color, turnOrder));
    playerCount++;
  }

  public void newPlayerFrom (final PlayerBuilder builder)
  {
    Arguments.checkIsNotNull (builder, "builder");

    playerSetBuilder.add (builder.build ());
    playerCount++;
  }

  public int getPlayerCount ()
  {
    return playerCount;
  }

  ImmutableSet <Player> getPlayers ()
  {
    return playerSetBuilder.build ();
  }

  /*
   * PlayerBuilder is public and static so that the type is visible both to the PlayerFactory.builder convenience method
   * and to external callers of the builder method.
   */
  public static final class PlayerBuilder
  {
    private final String name;
    private final Id id;
    private final PersonSentience sentience;
    private PlayerColor color = PlayerColor.UNKNOWN;
    private PlayerTurnOrder turnOrder = PlayerTurnOrder.UNKNOWN;

    public PlayerBuilder (final String name, final PersonSentience sentience)
    {
      Arguments.checkIsNotNull (name, "name");
      Arguments.checkIsNotNull (sentience, "sentience");

      this.name = name;
      this.sentience = sentience;
      id = IdGenerator.generateUniqueId ();
    }

    public PlayerBuilder color (final PlayerColor color)
    {
      Arguments.checkIsNotNull (color, "color");

      this.color = color;

      return this;
    }

    public PlayerBuilder turnOrder (final PlayerTurnOrder turnOrder)
    {
      Arguments.checkIsNotNull (turnOrder, "turnOrder");

      this.turnOrder = turnOrder;

      return this;
    }

    public PlayerFactory toFactory ()
    {
      final PlayerFactory factory = new PlayerFactory ();
      factory.newPlayerFrom (this);
      return factory;
    }

    Player build ()
    {
      return new DefaultPlayer (name, id, sentience, color, turnOrder);
    }
  }
}
