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
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Comparator;

public interface PlayerModel
{
  void addArmiesToHandOf (final Id playerId, final int armies);

  void addArmyToHandOf (final Id playerId);  @Override
  String toString ();

  void addCardsToHandOf (final Id playerId, final int cards);

  void addCardToHandOf (final Id playerId);

  boolean canAddArmiesToHandOf (final Id playerId, final int armies);

  boolean canAddArmyToHandOf (final Id playerId);

  boolean canAddCardsToHandOf (final Id playerId, final int cards);

  boolean canAddCardToHandOf (final Id playerId);

  boolean canRemoveArmiesFromHandOf (final Id playerId, final int armies);

  boolean canRemoveArmyFromHandOf (final Id playerId);

  boolean canRemoveCardsFromHandOf (final Id playerId, final int cards);

  boolean canRemoveCardFromHandOf (final Id playerId);

  /**
   * @return Ordered data about any players who had their turn order changed as a result of changing the turn order of
   *         the specified player, ordered by old turn order, will be empty if the specified player's turn order wasn't
   *         changed for any reason, otherwise always includes the specified player, and optionally another player who
   *         previously possessed the specified turn order. Never contains any duplicate players (by {@link Id}).
   */
  ImmutableSet <PlayerTurnOrderMutation> changeTurnOrderOfPlayer (final Id playerId, final PlayerTurnOrder toTurnOrder);

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

  void removeArmyFromHandOf (final Id playerId);

  void removeCardsFromHandOf (final Id playerId, final int cards);

  void removeCardFromHandOf (final Id playerId);

  ImmutableSet <PlayerJoinGameStatus> requestToAdd (final PlayerFactory players);

  /**
   * @return Ordered data about any players who had their turn order changed as a result of removing the specified
   *         player, ordered by old turn order, will be empty if no players were affected, or if the specified player
   *         wasn't removed for any reason. Never includes the removed player. Never contains any duplicate players (by
   *         {@link Id}).
   */
  ImmutableSet <PlayerTurnOrderMutation> remove (final Id playerId);

  /**
   * @return Ordered data about any players who had their turn order changed as a result of removing the specified
   *         player, ordered by old turn order, will be empty if no players were affected, or if the specified player
   *         wasn't removed for any reason. Never includes the removed player. Never contains any duplicate players (by
   *         {@link Id}).
   */
  ImmutableSet <PlayerTurnOrderMutation> removeByColor (final PlayerColor color);

  /**
   * @return Ordered data about any players who had their turn order changed as a result of removing the specified
   *         player, ordered by old turn order, will be empty if no players were affected, or if the specified player
   *         wasn't removed for any reason. Never includes the removed player. Never contains any duplicate players (by
   *         {@link Id}).
   */
  ImmutableSet <PlayerTurnOrderMutation> removeById (final Id id);

  /**
   * @return Ordered data about any players who had their turn order changed as a result of removing the specified
   *         player, ordered by old turn order, will be empty if no players were affected, or if the specified player
   *         wasn't removed for any reason. Never includes the removed player. Never contains any duplicate players (by
   *         {@link Id}).
   */
  ImmutableSet <PlayerTurnOrderMutation> removeByName (final String name);

  /**
   * @return Ordered data about any players who had their turn order changed as a result of removing the specified
   *         player, ordered by old turn order, will be empty if no players were affected, or if the specified player
   *         wasn't removed for any reason. Never includes the removed player. Never contains any duplicate players (by
   *         {@link Id}).
   */
  ImmutableSet <PlayerTurnOrderMutation> removeByTurnOrder (final PlayerTurnOrder turnOrder);

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

  final class PlayerTurnOrderMutation
  {
    public static final Comparator <PlayerTurnOrderMutation> ORDER_BY_OLD_TURN_ORDER = new Comparator <PlayerTurnOrderMutation> ()
    {
      @Override
      public int compare (final PlayerTurnOrderMutation o1, final PlayerTurnOrderMutation o2)
      {
        Arguments.checkIsNotNull (o1, "o1");
        Arguments.checkIsNotNull (o2, "o2");

        return Integer.compare (o1.getOldTurnOrder (), o2.getOldTurnOrder ());
      }
    };

    private final PlayerPacket player;
    private final int oldTurnOrder;

    PlayerTurnOrderMutation (final PlayerPacket player, final int oldTurnOrder)
    {
      Arguments.checkIsNotNull (player, "player");
      Arguments.checkIsNotNull (oldTurnOrder, "oldTurnOrder");
      Preconditions.checkIsTrue (player.doesNotHave (oldTurnOrder), "Player: [{}] must not have old turn order: [{}].",
                                 player, oldTurnOrder);

      this.player = player;
      this.oldTurnOrder = oldTurnOrder;
    }

    @Override
    public int hashCode ()
    {
      return player.hashCode ();
    }

    @Override
    public boolean equals (final Object o)
    {
      if (this == o) return true;
      if (o == null || getClass () != o.getClass ()) return false;

      final PlayerTurnOrderMutation mutation = (PlayerTurnOrderMutation) o;

      return player.equals (mutation.player);
    }

    public int getNewTurnOrder ()
    {
      return player.getTurnOrder ();
    }

    public int getOldTurnOrder ()
    {
      return oldTurnOrder;
    }    @Override
    public String toString ()
    {
      return Strings.format ("{}: Player: [{}] | OldTurnOrder: [{}]", getClass ().getSimpleName (), player,
                             oldTurnOrder);
    }

    public PlayerPacket getPlayer ()
    {
      return player;
    }



  }



}
