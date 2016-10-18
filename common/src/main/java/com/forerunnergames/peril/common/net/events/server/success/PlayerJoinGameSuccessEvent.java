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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastSuccessEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class PlayerJoinGameSuccessEvent extends AbstractPlayerEvent implements BroadcastSuccessEvent
{
  private final int playerLimit;
  private final PersonIdentity identity;
  private final ImmutableSet <PlayerPacket> playersInGame;

  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final PersonIdentity identity,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final int playerLimit)
  {
    super (player);

    Arguments.checkIsNotNull (identity, "identity");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");

    this.identity = identity;
    this.playersInGame = playersInGame;
    this.playerLimit = playerLimit;
  }

  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final int playerLimit)
  {
    super (player);

    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");

    identity = PersonIdentity.UNKNOWN;
    this.playersInGame = playersInGame;
    this.playerLimit = playerLimit;
  }

  public PersonIdentity getIdentity ()
  {
    return identity;
  }

  public ImmutableSet <PlayerPacket> getPlayersInGame ()
  {
    return playersInGame;
  }

  public boolean gameIsFull ()
  {
    return playersInGame.size () == playerLimit;
  }

  public int getPlayersNeededToMakeGameFull ()
  {
    return playerLimit - playersInGame.size ();
  }

  public int getPlayerCount ()
  {
    return playersInGame.size ();
  }

  public int getPlayerLimit ()
  {
    return playerLimit;
  }

  public ImmutableSet <PlayerPacket> getOtherPlayersInGame ()
  {
    return ImmutableSet.copyOf (Sets.filter (playersInGame, new Predicate <PlayerPacket> ()
    {
      @Override
      public boolean apply (final PlayerPacket input)
      {
        return input.isNot (getPlayer ());
      }
    }));
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | PersonIdentity: {} | PlayersInGame: {} | PlayerLimit: {}", super.toString (),
                           identity, playersInGame, playerLimit);
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameSuccessEvent ()
  {
    identity = null;
    playersInGame = null;
    playerLimit = 0;
  }
}
