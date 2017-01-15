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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerRestoreGameStateEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;

public abstract class AbstractPlayerRestoreGameStateEvent extends AbstractPlayerEvent
        implements PlayerRestoreGameStateEvent
{
  private final PlayerPacket currentPlayer;
  private final GamePhase currentGamePhase;
  private final int currentGameRound;
  private final GameRules gameRules;
  private final CardSetPacket cardsInHand;
  private final ImmutableSet <CardSetPacket> availableTradeIns;
  private final ImmutableMap <CountryPacket, PlayerPacket> countriesToPlayers;

  public AbstractPlayerRestoreGameStateEvent (final PlayerPacket selfPlayer,
                                              final PlayerPacket currentPlayer,
                                              final GamePhase currentGamePhase,
                                              final int currentGameRound,
                                              final GameRules gameRules,
                                              final CardSetPacket cardsInHand,
                                              final ImmutableSet <CardSetPacket> availableTradeIns,
                                              final ImmutableMap <CountryPacket, PlayerPacket> countriesToPlayers)
  {
    super (selfPlayer);

    Arguments.checkIsNotNull (currentPlayer, "currentPlayer");
    Arguments.checkIsNotNull (currentGamePhase, "currentGamePhase");
    Arguments.checkIsNotNegative (currentGameRound, "currentGameRound");
    Arguments.checkIsNotNull (gameRules, "gameRules");
    Arguments.checkIsNotNull (cardsInHand, "cardsInHand");
    Arguments.checkIsNotNull (availableTradeIns, "availableTradeIns");
    Arguments.checkHasNoNullElements (availableTradeIns, "availableTradeIns");
    Arguments.checkIsNotNull (countriesToPlayers, "countriesToPlayers");
    Arguments.checkHasNoNullKeysOrValues (countriesToPlayers, "countriesToPlayers");

    this.currentPlayer = currentPlayer;
    this.currentGamePhase = currentGamePhase;
    this.currentGameRound = currentGameRound;
    this.gameRules = gameRules;
    this.cardsInHand = cardsInHand;
    this.availableTradeIns = availableTradeIns;
    this.countriesToPlayers = countriesToPlayers;
  }

  @Override
  public PlayerPacket getSelfPlayer ()
  {
    return getPerson ();
  }

  @Override
  public PlayerPacket getCurrentPlayer ()
  {
    return currentPlayer;
  }

  @Override
  public GamePhase getCurrentGamePhase ()
  {
    return currentGamePhase;
  }

  @Override
  public int getCurrentGameRound ()
  {
    return currentGameRound;
  }

  @Override
  public GameRules getGameRules ()
  {
    return gameRules;
  }

  @Override
  public CardSetPacket getCardsInHand ()
  {
    return cardsInHand;
  }

  @Override
  public ImmutableSet <CardSetPacket> getAvailableTradeIns ()
  {
    return availableTradeIns;
  }

  @Override
  public ImmutableMap <CountryPacket, PlayerPacket> getCountriesToPlayers ()
  {
    return countriesToPlayers;
  }

  @Override
  public ImmutableSet <Map.Entry <CountryPacket, PlayerPacket>> getCountriesToPlayerEntries ()
  {
    return countriesToPlayers.entrySet ();
  }

  @Override
  public int getSelfOwnedCountryCount ()
  {
    return countriesToPlayers.asMultimap ().inverse ().get (getPerson ()).size ();
  }

  @Override
  public PlayerPacket getOwnerOf (final CountryPacket country)
  {
    Arguments.checkIsNotNull (country, "country");

    final PlayerPacket owner = countriesToPlayers.get (country);

    if (owner == null)
    {
      Exceptions.throwIllegalArg ("Cannot find owner of country: [{}]", country);
    }

    return owner;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{} | CurrentPlayer: [{}] | CurrentPhase: [{}] | CurrentRound: [{}] | GameRules: [{}] | CardsInHand: [{}] | "
                                   + "AvailableTradeIns: [{}] | CountriesToPlayers: [{}]",
                           super.toString (), currentPlayer, currentGamePhase, currentGameRound, gameRules, cardsInHand,
                           availableTradeIns, countriesToPlayers);
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerRestoreGameStateEvent ()
  {
    currentPlayer = null;
    currentGamePhase = null;
    currentGameRound = 0;
    gameRules = null;
    cardsInHand = null;
    availableTradeIns = null;
    countriesToPlayers = null;
  }
}
