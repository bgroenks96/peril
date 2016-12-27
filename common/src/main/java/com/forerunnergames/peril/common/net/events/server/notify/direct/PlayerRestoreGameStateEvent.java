package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DirectNotificiationEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class PlayerRestoreGameStateEvent extends AbstractPlayerEvent implements DirectNotificiationEvent
{
  private final PlayerPacket currentPlayerInTurn;
  private final int currentRound;
  private final ImmutableMap <CountryPacket, PlayerPacket> countriesToPlayers;
  private final CardSetPacket cardsInHand;
  private final ImmutableSet <CardSetPacket> availableTradeIns;

  public PlayerRestoreGameStateEvent (final PlayerPacket player,
                                      final PlayerPacket currentPlayerInTurn,
                                      final int currentRound,
                                      final ImmutableMap <CountryPacket, PlayerPacket> countriesToPlayers,
                                      final CardSetPacket cardsInHand,
                                      final ImmutableSet <CardSetPacket> availableTradeIns)
  {
    super (player);

    Arguments.checkIsNotNull (currentPlayerInTurn, "currentPlayerInTurn");
    Arguments.checkIsNotNegative (currentRound, "currentRound");
    Arguments.checkIsNotNull (countriesToPlayers, "countriesToPlayers");
    Arguments.checkIsNotNull (cardsInHand, "cardsInHand");
    Arguments.checkIsNotNull (availableTradeIns, "availableTradeIns");

    this.currentPlayerInTurn = currentPlayerInTurn;
    this.currentRound = currentRound;
    this.countriesToPlayers = countriesToPlayers;
    this.cardsInHand = cardsInHand;
    this.availableTradeIns = availableTradeIns;
  }

  public PlayerPacket getCurrentPlayerInTurn ()
  {
    return currentPlayerInTurn;
  }

  public int getCurrentRound ()
  {
    return currentRound;
  }

  public ImmutableMap <CountryPacket, PlayerPacket> getCountriesToPlayers ()
  {
    return countriesToPlayers;
  }

  public CardSetPacket getCardsInHand ()
  {
    return cardsInHand;
  }

  public ImmutableSet <CardSetPacket> getAvailableTradeIns ()
  {
    return availableTradeIns;
  }

  public PlayerPacket getOwner (final CountryPacket country)
  {
    return countriesToPlayers.get (country);
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{} | CurrentPlayer: [{}] | CurrentRoun: {} | CountriesToPlayers: [{}] | CardsInHand: [{}] | AvailableTradeIns: [{}]",
                           super.toString (), currentPlayerInTurn, currentRound, countriesToPlayers, cardsInHand,
                           availableTradeIns);
  }

  @RequiredForNetworkSerialization
  private PlayerRestoreGameStateEvent ()
  {
    currentPlayerInTurn = null;
    currentRound = 0;
    countriesToPlayers = null;
    cardsInHand = null;
    availableTradeIns = null;
  }
}
