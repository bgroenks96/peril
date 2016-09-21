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
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

public interface PlayerModel
{
  void addArmiesToHandOf (final Id playerId, final int armies);

  void addCardsToHandOf (final Id playerId, final int cards);

  void addCardToHandOf (final Id playerId);

  boolean canAddArmiesToHandOf (final Id playerId, final int armies);

  boolean canAddCardsToHandOf (final Id playerId, final int cards);

  boolean canAddCardToHandOf (final Id playerId);

  boolean canRemoveArmiesFromHandOf (final Id playerId, final int armies);

  boolean canRemoveCardsFromHandOf (final Id playerId, final int cards);

  boolean canRemoveCardFromHandOf (final Id playerId);

  void changeTurnOrderOfPlayer (final Id playerId, final PlayerTurnOrder toTurnOrder);

  boolean existsPlayerWith (final Id id);

  boolean existsPlayerWith (final String name);

  boolean existsPlayerWith (final PlayerColor color);

  boolean existsPlayerWith (final PlayerTurnOrder turnOrder);

  boolean existsPlayerWithName (final String name);

  int getArmiesInHand (final Id playerId);

  int getCardsInHand (final Id playerId);

  int getPlayerCount ();

  int getPlayerLimit ();

  ImmutableSet <PlayerPacket> getPlayerPackets ();

  ImmutableSet <Id> getPlayerIds ();

  ImmutableSortedSet <PlayerPacket> getTurnOrderedPlayers ();

  ImmutableSet <PlayerPacket> getAllPlayersExcept (final Id playerId);

  boolean hasArmiesInHandOf (final Id playerId, final int armies);

  boolean hasCardsInHandOf (final Id playerId, final int cards);

  boolean isEmpty ();

  boolean isFull ();

  boolean isNotEmpty ();

  boolean isNotFull ();

  boolean playerCountIs (final int count);

  boolean playerCountIsNot (final int count);

  boolean playerLimitIs (final int limit);

  boolean playerLimitIsAtLeast (final int limit);

  Id playerWith (final String name);

  Id playerWith (final PlayerColor color);

  Id playerWith (final PlayerTurnOrder turnOrder);

  PlayerPacket playerPacketWith (final Id id);

  PlayerPacket playerPacketWith (final String name);

  PlayerPacket playerPacketWith (final PlayerColor color);

  PlayerPacket playerPacketWith (final PlayerTurnOrder turnOrder);

  PlayerPacket playerPacketWithName (final String name);

  String nameOf (final Id player);

  PlayerColor colorOf (final Id player);

  PlayerTurnOrder turnOrderOf (final Id player);

  Id idOf (final String playerName);

  void removeAllArmiesFromHandsOfAllPlayers ();

  void removeAllCardsFromHandsOfAllPlayers ();

  void removeArmiesFromHandOf (final Id playerId, final int armies);

  void removeCardsFromHandOf (final Id playerId, final int cards);

  void removeCardFromHandOf (final Id playerId);

  ImmutableSet <PlayerJoinGameStatus> requestToAdd (final PlayerFactory players);

  void remove (final Id playerId);

  void removeByColor (final PlayerColor color);

  void removeById (final Id id);

  void removeByName (final String name);

  void removeByTurnOrder (final PlayerTurnOrder turnOrder);

  @Override
  String toString ();

  class PlayerJoinGameStatus implements Result.ReturnStatus <PlayerJoinGameDeniedEvent.Reason>
  {
    private final PlayerPacket player;
    private final Result <PlayerJoinGameDeniedEvent.Reason> result;

    public PlayerJoinGameStatus (final PlayerPacket player, final Result <PlayerJoinGameDeniedEvent.Reason> result)
    {
      Arguments.checkIsNotNull (player, "player");
      Arguments.checkIsNotNull (result, "result");

      this.player = player;
      this.result = result;
    }

    @Override
    public Result <PlayerJoinGameDeniedEvent.Reason> getResult ()
    {
      return result;
    }

    public PlayerPacket getPlayer ()
    {
      return player;
    }

    public PlayerJoinGameDeniedEvent.Reason getFailureReason ()
    {
      return result.getFailureReason ();
    }

    public boolean failed ()
    {
      return result.failed ();
    }

    public boolean succeeded ()
    {
      return result.succeeded ();
    }
  }
}
