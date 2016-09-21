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

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.math.IntMath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class DefaultPlayerModel implements PlayerModel
{
  private final Map <Id, Player> players = new HashMap <> ();
  private final GameRules rules;

  public DefaultPlayerModel (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  @Override
  public void addArmiesToHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canAddArmiesToHandOf (playerId, armies),
                               "Cannot add " + armies + " armies to hand of player with id: [" + playerId
                                       + "]. That player already has " + getArmiesInHand (playerId)
                                       + " armies in hand. The maximum armies allowed in a player's hand is "
                                       + rules.getMaxArmiesInHand () + ".");

    modelPlayerWith (playerId).addArmiesToHand (armies);
  }

  @Override
  public void addArmyToHandOf (final Id playerId)
  {
    addArmiesToHandOf (playerId, 1);
  }

  @Override
  public void addCardsToHandOf (final Id playerId, final int cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (cards, "cards");
    Preconditions.checkIsTrue (canAddCardsToHandOf (playerId, cards),
                               "Cannot add " + cards + " cards to hand of player with id: [" + playerId
                                       + "]. That player already has " + getCardsInHand (playerId)
                                       + " cards in hand. The maximum cards allowed in a player's hand is "
                                       + rules.getAbsoluteMaxCardsInHand () + ".");

    modelPlayerWith (playerId).addCardsToHand (cards);
  }

  @Override
  public void addCardToHandOf (final Id playerId)
  {
    addCardsToHandOf (playerId, 1);
  }

  @Override
  public boolean canAddArmiesToHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerPacketWith (playerId).getArmiesInHand () <= IntMath.checkedSubtract (rules.getMaxArmiesInHand (),
                                                                                      armies);
  }

  @Override
  public boolean canAddArmyToHandOf (final Id playerId)
  {
    return canAddArmiesToHandOf (playerId, 1);
  }

  @Override
  public boolean canAddCardsToHandOf (final Id playerId, final int cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (cards, "cards");

    return modelPlayerWith (playerId).getCardsInHand () <= IntMath.checkedSubtract (rules.getAbsoluteMaxCardsInHand (),
                                                                                    cards);
  }

  @Override
  public boolean canAddCardToHandOf (final Id playerId)
  {
    return canAddCardsToHandOf (playerId, 1);
  }

  @Override
  public boolean canRemoveArmiesFromHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerPacketWith (playerId).getArmiesInHand () >= IntMath.checkedAdd (rules.getMinArmiesInHand (), armies);
  }

  @Override
  public boolean canRemoveArmyFromHandOf (final Id playerId)
  {
    return canRemoveArmiesFromHandOf (playerId, 1);
  }

  @Override
  public boolean canRemoveCardsFromHandOf (final Id playerId, final int cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (cards, "cards");

    return modelPlayerWith (playerId).getCardsInHand () >= IntMath.checkedAdd (rules.getAbsoluteMinCardsInHand (), cards);
  }

  @Override
  public boolean canRemoveCardFromHandOf (final Id playerId)
  {
    return canRemoveCardsFromHandOf (playerId, 1);
  }

  @Override
  public void changeTurnOrderOfPlayer (final Id playerId, final PlayerTurnOrder toTurnOrder)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (toTurnOrder, "toTurnOrder");
    Arguments.checkIsFalse (toTurnOrder.is (PlayerTurnOrder.UNKNOWN), "Invalid player turn order.");

    final Player player = modelPlayerWith (playerId);

    if (player.has (toTurnOrder)) return;

    if (existsPlayerWith (toTurnOrder))
    {
      final Player old = modelPlayerWith (toTurnOrder);
      old.setTurnOrder (PlayerTurnOrder.UNKNOWN);
      player.setTurnOrder (toTurnOrder);
      old.setTurnOrder (nextAvailableTurnOrder ());
    }
    else
    {
      player.setTurnOrder (toTurnOrder);
    }
  }

  @Override
  public boolean existsPlayerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    return players.containsKey (id);
  }

  @Override
  public boolean existsPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return true;
    }

    return false;
  }

  @Override
  public boolean existsPlayerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    for (final Player player : players ())
    {
      if (player.has (color)) return true;
    }

    return false;
  }

  @Override
  public boolean existsPlayerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    for (final Player player : players ())
    {
      if (player.has (turnOrder)) return true;
    }

    return false;
  }

  @Override
  public boolean existsPlayerWithName (final String name)
  {
    return existsPlayerWith (name);
  }

  @Override
  public int getArmiesInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerPacketWith (playerId).getArmiesInHand ();
  }

  @Override
  public int getCardsInHand (final Id playerId)
  {
    return modelPlayerWith (playerId).getCardsInHand ();
  }

  @Override
  public int getPlayerCount ()
  {
    return players.size ();
  }

  @Override
  public int getPlayerLimit ()
  {
    return rules.getPlayerLimit ();
  }

  @Override
  public ImmutableSet <Id> getPlayerIds ()
  {
    final ImmutableSet.Builder <Id> playerIds = ImmutableSet.builder ();
    for (final Player player : players ())
    {
      playerIds.add (player.getId ());
    }
    return playerIds.build ();
  }

  @Override
  public ImmutableSet <PlayerPacket> getPlayerPackets ()
  {
    return PlayerPackets.fromPlayers (players ());
  }

  @Override
  public ImmutableSortedSet <PlayerPacket> getTurnOrderedPlayers ()
  {
    return ImmutableSortedSet.copyOf (PlayerPacket.TURN_ORDER_COMPARATOR, PlayerPackets.fromPlayers (players ()));
  }

  @Override
  public ImmutableSet <PlayerPacket> getAllPlayersExcept (final Id playerId)
  {
    Preconditions.checkIsTrue (existsPlayerWith (playerId),
                               Strings.format ("No player with id [{}] exists.", playerId));

    final Player player = modelPlayerWith (playerId);
    return PlayerPackets.fromPlayers (Collections2.filter (players.values (), not (equalTo (player))));
  }

  @Override
  public boolean hasArmiesInHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerPacketWith (playerId).hasArmiesInHand (armies);
  }

  @Override
  public boolean hasCardsInHandOf (final Id playerId, final int cards)
  {
    return modelPlayerWith (playerId).hasCardsInHand (cards);
  }

  @Override
  public boolean isEmpty ()
  {
    return players.isEmpty ();
  }

  @Override
  public boolean isFull ()
  {
    return !players.isEmpty () && players.size () >= rules.getPlayerLimit ();
  }

  @Override
  public boolean isNotEmpty ()
  {
    return !isEmpty ();
  }

  @Override
  public boolean isNotFull ()
  {
    return !isFull ();
  }

  @Override
  public boolean playerCountIs (final int count)
  {
    return players.size () == count;
  }

  @Override
  public boolean playerCountIsNot (final int count)
  {
    return !playerCountIs (count);
  }

  @Override
  public boolean playerLimitIs (final int limit)
  {
    return rules.getPlayerLimit () == limit;
  }

  @Override
  public boolean playerLimitIsAtLeast (final int limit)
  {
    return rules.getPlayerLimit () >= limit;
  }

  @Override
  public Id playerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return player.getId ();
    }

    throw new IllegalStateException ("Cannot find any player named: [" + name + "].");
  }

  @Override
  public Id playerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    for (final Player player : players ())
    {
      if (player.has (color)) return player.getId ();
    }

    throw new IllegalStateException ("Cannot find any player with color: [" + color + "].");
  }

  @Override
  public Id playerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    for (final Player player : players ())
    {
      if (player.has (turnOrder)) return player.getId ();
    }

    throw new IllegalStateException ("Cannot find any player with turn order: [" + turnOrder + "].");
  }

  @Override
  public PlayerPacket playerPacketWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    final Player player = players.get (id);

    if (player == null) throw new IllegalStateException ("Cannot find any player with id [" + id + "].");

    return PlayerPackets.from (player);
  }

  @Override
  public PlayerPacket playerPacketWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return PlayerPackets.from (player);
    }

    throw new IllegalStateException ("Cannot find any player named: [" + name + "].");
  }

  @Override
  public PlayerPacket playerPacketWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    for (final Player player : players ())
    {
      if (player.has (color)) return PlayerPackets.from (player);
    }

    throw new IllegalStateException ("Cannot find any player with color: [" + color + "].");
  }

  @Override
  public PlayerPacket playerPacketWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    for (final Player player : players ())
    {
      if (player.has (turnOrder)) return PlayerPackets.from (player);
    }

    throw new IllegalStateException ("Cannot find any player with turn order: [" + turnOrder + "].");
  }

  @Override
  public PlayerPacket playerPacketWithName (final String name)
  {
    return playerPacketWith (name);
  }

  @Override
  public String nameOf (final Id playerId)
  {
    return modelPlayerWith (playerId).getName ();
  }

  @Override
  public PlayerColor colorOf (final Id playerId)
  {
    return modelPlayerWith (playerId).getColor ();
  }

  @Override
  public PlayerTurnOrder turnOrderOf (final Id playerId)
  {
    return modelPlayerWith (playerId).getTurnOrder ();
  }

  @Override
  public Id idOf (final String playerName)
  {
    return modelPlayerWith (playerName).getId ();
  }

  @Override
  public void removeAllArmiesFromHandsOfAllPlayers ()
  {
    for (final Player player : players ())
    {
      player.removeAllArmiesFromHand ();
    }
  }

  @Override
  public void removeAllCardsFromHandsOfAllPlayers ()
  {
    for (final Player player : players ())
    {
      player.removeAllCardsFromHand ();
    }
  }

  @Override
  public void removeArmiesFromHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canRemoveArmiesFromHandOf (playerId, armies),
                               "Cannot remove " + armies + " armies from hand of player with id: [" + playerId
                                       + "]. That player only has " + getArmiesInHand (playerId)
                                       + " armies in hand. The minimum armies allowed in a player's hand is "
                                       + rules.getMinArmiesInHand () + ".");

    modelPlayerWith (playerId).removeArmiesFromHand (armies);
  }

  @Override
  public void removeArmyFromHandOf (final Id playerId)
  {
    removeArmiesFromHandOf (playerId, 1);
  }

  @Override
  public void removeCardsFromHandOf (final Id playerId, final int cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (cards, "cards");
    Preconditions.checkIsTrue (canRemoveCardsFromHandOf (playerId, cards),
                               "Cannot remove " + cards + " cards from hand of player with id: [" + playerId
                                       + "]. That player only has " + getCardsInHand (playerId)
                                       + " cards in hand. The minimum cards allowed in a player's hand is "
                                       + rules.getAbsoluteMinCardsInHand () + ".");

    modelPlayerWith (playerId).removeCardsFromHand (cards);
  }

  @Override
  public void removeCardFromHandOf (final Id playerId)
  {
    removeCardsFromHandOf (playerId, 1);
  }

  @Override
  public ImmutableSet <PlayerJoinGameStatus> requestToAdd (final PlayerFactory players)
  {
    Arguments.checkIsNotNull (players, "players");

    final ImmutableSet.Builder <PlayerJoinGameStatus> joinGameResults = ImmutableSet.builder ();
    for (final Player player : players.getPlayers ())
    {
      Result <PlayerJoinGameDeniedEvent.Reason> result = Result.success ();
      // @formatter:off
      if (existsPlayerWith (player.getName ())) result = Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME);
      if (existsPlayerWith (player.getColor ())) result = Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_COLOR);
      if (existsPlayerWith (player.getTurnOrder ())) result = Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_TURN_ORDER);
      if (!GameSettings.isValidPlayerNameWithOptionalClanTag (player.getName ())) result = Result.failure (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME);
      if (isFull ()) result = Result.failure (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL);
      // @formatter:on
      if (result.succeeded ()) add (player);
      joinGameResults.add (new PlayerJoinGameStatus (PlayerPackets.from (player), result));
    }

    return joinGameResults.build ();
  }

  @Override
  public void remove (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    if (!existsPlayerWith (playerId)) return;

    final Player player = modelPlayerWith (playerId);
    deregister (player);
    fixTurnOrdersAfterRemovalOfPlayer (player);
  }

  @Override
  public void removeByColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    if (!existsPlayerWith (color)) return;

    remove (modelPlayerWith (color).getId ());
  }

  @Override
  public void removeById (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (!existsPlayerWith (id)) return;

    remove (modelPlayerWith (id).getId ());
  }

  @Override
  public void removeByName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    if (!existsPlayerWith (name)) return;

    remove (modelPlayerWith (name).getId ());
  }

  @Override
  public void removeByTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    if (!existsPlayerWith (turnOrder)) return;

    remove (modelPlayerWith (turnOrder).getId ());
  }

  Player modelPlayerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    final Player player = players.get (id);

    if (player == null) throw new IllegalStateException ("Cannot find any player with id [" + id + "].");

    return player;
  }

  Player modelPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return player;
    }

    throw new IllegalStateException ("Cannot find any player named: [" + name + "].");
  }

  Player modelPlayerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    for (final Player player : players ())
    {
      if (player.has (color)) return player;
    }

    throw new IllegalStateException ("Cannot find any player with color: [" + color + "].");
  }

  Player modelPlayerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    for (final Player player : players ())
    {
      if (player.has (turnOrder)) return player;
    }

    throw new IllegalStateException ("Cannot find any player with turn order: [" + turnOrder + "].");
  }

  Player modelPlayerWithName (final String name)
  {
    return modelPlayerWith (name);
  }

  private void add (final Player player)
  {
    if (player.has (PlayerColor.UNKNOWN))
    {
      player.setColor (nextAvailableColor ());
    }
    if (player.has (PlayerTurnOrder.UNKNOWN))
    {
      player.setTurnOrder (nextAvailableTurnOrder ());
    }

    register (player);
  }

  private void deregister (final Player player)
  {
    assert players.containsValue (player);

    players.remove (player.getId ());
  }

  private void fixTurnOrdersAfterRemovalOfPlayer (final Player removedPlayer)
  {
    // Ensure the removed player is really removed, or this will not work.
    assert !players.containsValue (removedPlayer);

    final int danglingTurnOrderPosition = removedPlayer.getTurnOrderPosition ();

    // Lower the turn orders of all players having a turn order higher than the discontinuity, by one.
    // For example, consider where the 2nd player is removed, leaving: 1st, ___, 3rd, 4th, 5th
    // 2nd is the dangling turn order position
    // All positions after the dangling must be bumped up by one, IN ORDER FROM LOWEST TO HIGHEST, to avoid conflicts:
    // 1st, 3rd => 2nd, 4th => 3rd, 5th => 4th, leaving:
    // 1st, 2nd, 3rd, 4th, effectively filling the gap left by the player that was removed.
    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!existsPlayerWith (turnOrder)) continue;
      if (turnOrder.getPosition () <= danglingTurnOrderPosition) continue;

      modelPlayerWith (turnOrder).setTurnOrderByPosition (turnOrder.getPosition () - 1);
    }
  }

  private PlayerColor nextAvailableColor ()
  {
    for (final PlayerColor color : PlayerColor.VALID_VALUES)
    {
      if (!existsPlayerWith (color)) return color;
    }

    throw new IllegalStateException ("There are no available player colors.");
  }

  private PlayerTurnOrder nextAvailableTurnOrder ()
  {
    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!existsPlayerWith (turnOrder)) return turnOrder;
    }

    throw new IllegalStateException ("There are no available player turn orders.");
  }

  private Collection <Player> players ()
  {
    return players.values ();
  }

  private void register (final Player player)
  {
    assert !players.containsValue (player);
    assert player.doesNotHave (PlayerColor.UNKNOWN);
    assert player.doesNotHave (PlayerTurnOrder.UNKNOWN);

    players.put (player.getId (), player);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Rules [{}] | Players [{}]", getClass ().getSimpleName (), rules.getClass (),
                           players.values ());
  }
}
