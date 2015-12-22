package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
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

  ImmutableSet <PlayerPacket> getPlayerPackets ();

  ImmutableSet <Id> getPlayerIds ();

  ImmutableSortedSet <PlayerPacket> getTurnOrderedPlayers ();

  ImmutableSet <PlayerPacket> getAllPlayersExcept (final Id playerId);

  boolean hasArmiesInHandOf (final Id playerId, final int armies);

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

  PersonIdentity identityOf (final Id player);

  Id idOf (final String playerName);

  void removeAllArmiesFromHandsOfAllPlayers ();

  void removeArmiesFromHandOf (final Id playerId, final int armies);

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

    public PlayerPacket getPlayer ()
    {
      return player;
    }

    @Override
    public Result <PlayerJoinGameDeniedEvent.Reason> getResult ()
    {
      return result;
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
