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

import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPersonJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.UUID;

import javax.annotation.Nullable;

public final class PlayerJoinGameSuccessEvent extends AbstractPersonJoinGameSuccessEvent <PlayerPacket>
        implements PlayerEvent
{
  // only present if identity = self
  @Nullable
  private final UUID playerSecretId;

  /**
   * Convenience constructor for when person identity is unknown or not cared about, and there are no spectators in game
   * or spectators in game are unknown or not cared about.
   */
  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final PersonLimits personLimits)
  {
    super (player, playersInGame, personLimits);

    playerSecretId = null;
  }

  /**
   * Convenience constructor for when person identity is unknown or not cared about.
   */
  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                     final PersonLimits personLimits)
  {
    super (player, playersInGame, spectatorsInGame, personLimits);

    playerSecretId = null;
  }

  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final PersonIdentity identity,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                     final PersonLimits personLimits)
  {
    super (player, identity, playersInGame, spectatorsInGame, personLimits);

    playerSecretId = null;
  }

  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final UUID playerSecretId,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                     final PersonLimits personLimits)
  {
    super (player, PersonIdentity.SELF, playersInGame, spectatorsInGame, personLimits);

    Arguments.checkIsNotNull (playerSecretId, "playerSecretId");

    this.playerSecretId = playerSecretId;
  }

  @Override
  public PlayerColor getPlayerColor ()
  {
    return getPerson ().getColor ();
  }

  @Override
  public int getPlayerTurnOrder ()
  {
    return getPerson ().getTurnOrder ();
  }

  @Override
  public int getPlayerArmiesInHand ()
  {
    return getPerson ().getArmiesInHand ();
  }

  @Override
  public int getPlayerCardsInHand ()
  {
    return getPerson ().getCardsInHand ();
  }

  public ImmutableSet <PlayerPacket> getOtherPlayersInGame ()
  {
    return ImmutableSet.copyOf (Sets.filter (getPlayersInGame (), new Predicate <PlayerPacket> ()
    {
      @Override
      public boolean apply (final PlayerPacket input)
      {
        return input.isNot (getPerson ());
      }
    }));
  }

  @Nullable
  public UUID getPlayerSecretId ()
  {
    return playerSecretId;
  }

  public boolean hasSecretId ()
  {
    return playerSecretId != null && getIdentity () == PersonIdentity.SELF;
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameSuccessEvent ()
  {
    playerSecretId = null;
  }
}
