package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.shared.net.events.server.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

public interface PlayerModel
{
  void addArmiesToHandOf (final Id playerId, final int armies);

  boolean canAddArmiesToHandOf (final Id playerId, final int armies);

  boolean canRemoveArmiesFromHandOf (final Id playerId, final int armies);

  void changeTurnOrderOfPlayer (final Id playerId, final PlayerTurnOrder toTurnOrder);

  boolean existsPlayerWith (final Id id);

  boolean existsPlayerWith (final String name);

  boolean existsPlayerWith (final PlayerColor color);

  boolean existsPlayerWith (final PlayerTurnOrder turnOrder);

  boolean existsPlayerWith (final PersonIdentity identity);

  boolean existsPlayerWithName (final String name);

  int getArmiesInHand (final Id playerId);

  int getPlayerCount ();

  int getPlayerLimit ();

  ImmutableSet <Player> getPlayers ();

  ImmutableSortedSet <Player> getTurnOrderedPlayers ();

  ImmutableSet <Player> getAllPlayersExcept (final Player player);

  boolean hasArmiesInHandOf (final Id playerId, final int armies);

  boolean isEmpty ();

  boolean isFull ();

  boolean isNotEmpty ();

  boolean isNotFull ();

  boolean playerCountIs (final int count);

  boolean playerCountIsNot (final int count);

  boolean playerLimitIs (final int limit);

  boolean playerLimitIsAtLeast (final int limit);

  Player playerWith (final Id id);

  Player playerWith (final String name);

  Player playerWith (final PlayerColor color);

  Player playerWith (final PlayerTurnOrder turnOrder);

  Player playerWithName (final String name);

  void removeAllArmiesFromHandsOfAllPlayers ();

  void removeArmiesFromHandOf (final Id playerId, final int armies);

  Result <PlayerJoinGameDeniedEvent.Reason> requestToAdd (final Player player);

  Result <ChangePlayerColorDeniedEvent.Reason> requestToChangeColorOfPlayer (final Id playerId,
                                                                             final PlayerColor toColor);

  void remove (final Player player);

  void removeByColor (final PlayerColor color);

  void removeById (final Id id);

  void removeByName (final String name);

  void removeByTurnOrder (final PlayerTurnOrder turnOrder);

  @Override
  String toString ();
}
