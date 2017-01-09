/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPersonJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.UUID;

import javax.annotation.Nullable;

public final class PlayerJoinGameSuccessEvent extends AbstractPersonJoinGameSuccessEvent <PlayerPacket>
        implements PlayerEvent
{
  @Nullable
  private final UUID selfPlayerSecretId;

  /**
   * Convenience constructor for when person identity & self player secret id are unknown or not cared about, and there
   * are no spectators in game or spectators in game are unknown or not cared about.
   */
  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final PersonLimits personLimits)
  {
    super (player, playersInGame, personLimits);

    selfPlayerSecretId = null;
  }

  /**
   * Convenience constructor for when person identity & self player secret id are unknown or not cared about.
   * {@link #hasSelfPlayerSecretId()} will always return false. {@link #getSelfPlayerSecretId()} will always return
   * {@code null}.
   */
  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                     final PersonLimits personLimits)
  {
    super (player, playersInGame, spectatorsInGame, personLimits);

    selfPlayerSecretId = null;
  }

  /**
   * Constructor for {@link PersonIdentity#NON_SELF}, which cannot have a self player secret id.
   * {@link #hasSelfPlayerSecretId()} will always return false. {@link #getSelfPlayerSecretId()} will always return
   * {@code null}.
   *
   * Note: This constructor will also accept {@link PersonIdentity#UNKNOWN}, i.e., any value other than
   * {@link PersonIdentity#SELF}.
   *
   * @param identity
   *          Must not be {@link PersonIdentity#SELF}. See
   *          {@link #PlayerJoinGameSuccessEvent(PlayerPacket, UUID, ImmutableSet, ImmutableSet, PersonLimits)} for
   *          setting {@code PersonIdentity#SELF}, which must be accompanied by a self player secret id.
   */
  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final PersonIdentity identity,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                     final PersonLimits personLimits)
  {
    super (player, identity, playersInGame, spectatorsInGame, personLimits);

    Preconditions
            .checkIsTrue (identity != PersonIdentity.SELF,
                          "Cannot use this constructor for self player (i.e., PersonIdentity#SELF); secret id would not be set.");

    selfPlayerSecretId = null;
  }

  /**
   * Constructor for PersonIdentity#SELF, which must have a self player secret id. {@link #hasSelfPlayerSecretId()} will
   * always return true. {@link #getSelfPlayerSecretId()} will always return the specified {@code UUID}.
   *
   * @param selfPlayerSecretId
   *          Unique identifier used for rejoining a player to the game via
   *          {@link PlayerJoinGameRequestEvent#getPlayerSecretId()} if ever disconnected from the server. Must not be
   *          null.
   */
  public PlayerJoinGameSuccessEvent (final PlayerPacket player,
                                     final UUID selfPlayerSecretId,
                                     final ImmutableSet <PlayerPacket> playersInGame,
                                     final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                     final PersonLimits personLimits)
  {
    super (player, PersonIdentity.SELF, playersInGame, spectatorsInGame, personLimits);

    Arguments.checkIsNotNull (selfPlayerSecretId, "selfPlayerSecretId");

    this.selfPlayerSecretId = selfPlayerSecretId;
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
  public UUID getSelfPlayerSecretId ()
  {
    return selfPlayerSecretId;
  }

  public boolean hasSelfPlayerSecretId ()
  {
    return selfPlayerSecretId != null;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SelfPlayerSecretId: [{}]", super.toString (), selfPlayerSecretId);
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameSuccessEvent ()
  {
    selfPlayerSecretId = null;
  }
}
