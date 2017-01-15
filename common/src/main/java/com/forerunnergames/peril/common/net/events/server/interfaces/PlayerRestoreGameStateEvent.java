package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;

public interface PlayerRestoreGameStateEvent extends DirectPlayerNotificationEvent
{
  public PlayerPacket getSelfPlayer ();

  public PlayerPacket getCurrentPlayer ();

  public GamePhase getCurrentGamePhase ();

  public int getCurrentGameRound ();

  public GameRules getGameRules ();

  public CardSetPacket getCardsInHand ();

  public ImmutableSet <CardSetPacket> getAvailableTradeIns ();

  public ImmutableMap <CountryPacket, PlayerPacket> getCountriesToPlayers ();

  public ImmutableSet <Map.Entry <CountryPacket, PlayerPacket>> getCountriesToPlayerEntries ();

  public int getSelfOwnedCountryCount ();

  public PlayerPacket getOwnerOf (final CountryPacket country);
}
