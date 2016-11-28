/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PersonPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastSuccessEvent;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractPersonJoinGameSuccessEvent <T extends PersonPacket> extends AbstractPersonEvent <T>
        implements BroadcastSuccessEvent
{
  private final PersonLimits personLimits;
  private final PersonIdentity identity;
  private final ImmutableSet <PlayerPacket> playersInGame;
  private final ImmutableSet <SpectatorPacket> spectatorsInGame;

  /**
   * Convenience constructor for when person identity is unknown or not cared about, and there are no spectators in game
   * or spectators in game are unknown or not cared about.
   */
  protected AbstractPersonJoinGameSuccessEvent (final T person,
                                                final ImmutableSet <PlayerPacket> playersInGame,
                                                final PersonLimits personLimits)
  {
    this (person, playersInGame, ImmutableSet.<SpectatorPacket> of (), personLimits);
  }

  /**
   * Convenience constructor for when person identity is unknown or not cared about.
   */
  protected AbstractPersonJoinGameSuccessEvent (final T person,
                                                final ImmutableSet <PlayerPacket> playersInGame,
                                                final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                                final PersonLimits personLimits)
  {
    this (person, PersonIdentity.UNKNOWN, playersInGame, spectatorsInGame, personLimits);
  }

  protected AbstractPersonJoinGameSuccessEvent (final T person,
                                                final PersonIdentity identity,
                                                final ImmutableSet <PlayerPacket> playersInGame,
                                                final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                                final PersonLimits personLimits)
  {
    super (person);

    Arguments.checkIsNotNull (identity, "identity");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkIsNotNull (spectatorsInGame, "spectatorsInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (spectatorsInGame, "spectatorsInGame");
    Arguments.checkIsNotNull (personLimits, "personLimits");

    this.identity = identity;
    this.playersInGame = playersInGame;
    this.spectatorsInGame = spectatorsInGame;
    this.personLimits = personLimits;
  }

  @RequiredForNetworkSerialization
  protected AbstractPersonJoinGameSuccessEvent ()
  {
    identity = null;
    playersInGame = null;
    spectatorsInGame = null;
    personLimits = null;
  }

  public final PersonIdentity getIdentity ()
  {
    return identity;
  }

  public final boolean hasIdentity (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    return this.identity == identity;
  }

  public final ImmutableSet <PlayerPacket> getPlayersInGame ()
  {
    return playersInGame;
  }

  public final ImmutableSet <SpectatorPacket> getSpectatorsInGame ()
  {
    return spectatorsInGame;
  }

  public final boolean gameIsFullPlayers ()
  {
    return getPlayerCount () == getTotalPlayerLimit ();
  }

  public final boolean gameIsFullSpectators ()
  {
    return getSpectatorCount () == getSpetatorLimit ();
  }

  public final int getPlayersNeededToMakeGameFull ()
  {
    return getTotalPlayerLimit () - getPlayerCount ();
  }

  public final int getSpectatorsNeededToMakeGameFull ()
  {
    return getSpetatorLimit () - getSpectatorCount ();
  }

  public final int getPlayerCount ()
  {
    return playersInGame.size ();
  }

  public final int getSpectatorCount ()
  {
    return spectatorsInGame.size ();
  }

  public PersonLimits getPersonLimits ()
  {
    return personLimits;
  }

  public final int getTotalPlayerLimit ()
  {
    return personLimits.getTotalPlayerLimit ();
  }

  public final int getSpetatorLimit ()
  {
    return personLimits.getSpectatorLimit ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{} | PersonIdentity: [{}] | PlayerCount: [{}] | SpectatorCount: [{}] | PlayersInGame: [{}] | "
                                   + "SpectatorsInGame: [{}] | PersonLimits: [{}]",
                           super.toString (), identity, getPlayerCount (), getSpectatorCount (), playersInGame,
                           spectatorsInGame, personLimits);
  }
}
