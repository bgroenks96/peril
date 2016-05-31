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
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

public final class PlayerFactory
{
  private final ImmutableSet.Builder <Player> playerSetBuilder = ImmutableSet.builder ();
  private int playerCount = 0;

  public void newPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    playerSetBuilder.add (create (name));
    playerCount++;
  }

  public void newPlayerWith (final String name,
                             final PersonIdentity identity,
                             final PlayerColor color,
                             final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (identity, "identity");
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    playerSetBuilder.add (create (name, identity, color, turnOrder));
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

  static Player create (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return builder (name).build ();
  }

  static Player create (final String name,
                        final PersonIdentity identity,
                        final PlayerColor color,
                        final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (identity, "identity");
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return builder (name).identity (identity).color (color).turnOrder (turnOrder).build ();
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

  public static PlayerBuilder builder (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return new PlayerBuilder (playerName);
  }

  /*
   * PlayerBuilder is public and static so that the type is visible both to the PlayerFactory.builder convenience method
   * and to external callers of the builder method.
   */
  public static final class PlayerBuilder
  {
    private final String name;
    private final Id id;
    private PersonIdentity identity = PersonIdentity.UNKNOWN;
    private PlayerColor color = PlayerColor.UNKNOWN;
    private PlayerTurnOrder turnOrder = PlayerTurnOrder.UNKNOWN;

    public PlayerBuilder (final String name)
    {
      Arguments.checkIsNotNull (name, "name");

      this.name = name;
      id = IdGenerator.generateUniqueId ();
    }

    public PlayerBuilder color (final PlayerColor color)
    {
      Arguments.checkIsNotNull (color, "color");

      this.color = color;

      return this;
    }

    public PlayerBuilder identity (final PersonIdentity identity)
    {
      Arguments.checkIsNotNull (identity, "identity");

      this.identity = identity;

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
      return new DefaultPlayer (name, id, identity, color, turnOrder);
    }
  }
}
