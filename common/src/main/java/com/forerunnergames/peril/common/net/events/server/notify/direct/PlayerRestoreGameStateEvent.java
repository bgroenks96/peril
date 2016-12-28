/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DirectNotificiationEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;

public final class PlayerRestoreGameStateEvent extends AbstractPlayerEvent implements DirectNotificiationEvent
{
  private final PlayerPacket currentPlayer;
  private final int currentRound;
  private final CardSetPacket cardsInHand;
  private final ImmutableSet <CardSetPacket> availableTradeIns;
  private final ImmutableMap <CountryPacket, PlayerPacket> countriesToPlayers;

  public PlayerRestoreGameStateEvent (final PlayerPacket selfPlayer,
                                      final PlayerPacket currentPlayer,
                                      final int currentRound,
                                      final CardSetPacket cardsInHand,
                                      final ImmutableSet <CardSetPacket> availableTradeIns,
                                      final ImmutableMap <CountryPacket, PlayerPacket> countriesToPlayers)
  {
    super (selfPlayer);

    Arguments.checkIsNotNull (currentPlayer, "currentPlayer");
    Arguments.checkIsNotNegative (currentRound, "currentRound");
    Arguments.checkIsNotNull (cardsInHand, "cardsInHand");
    Arguments.checkIsNotNull (availableTradeIns, "availableTradeIns");
    Arguments.checkHasNoNullElements (availableTradeIns, "availableTradeIns");
    Arguments.checkIsNotNull (countriesToPlayers, "countriesToPlayers");
    Arguments.checkHasNoNullKeysOrValues (countriesToPlayers, "countriesToPlayers");

    this.currentPlayer = currentPlayer;
    this.currentRound = currentRound;
    this.cardsInHand = cardsInHand;
    this.availableTradeIns = availableTradeIns;
    this.countriesToPlayers = countriesToPlayers;
  }

  public PlayerPacket getCurrentPlayer ()
  {
    return currentPlayer;
  }

  public int getCurrentRound ()
  {
    return currentRound;
  }

  public CardSetPacket getCardsInHand ()
  {
    return cardsInHand;
  }

  public ImmutableSet <CardSetPacket> getAvailableTradeIns ()
  {
    return availableTradeIns;
  }

  public ImmutableMap <CountryPacket, PlayerPacket> getCountriesToPlayers ()
  {
    return countriesToPlayers;
  }

  public ImmutableSet <Map.Entry <CountryPacket, PlayerPacket>> getCountriesToPlayerEntries ()
  {
    return countriesToPlayers.entrySet ();
  }

  public int getSelfOwnedCountryCount ()
  {
    return countriesToPlayers.asMultimap ().inverse ().get (getPerson ()).size ();
  }

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
                           "{} | CurrentPlayer: [{}] | CurrentRound: [{}] | CardsInHand: [{}] | "
                                   + "AvailableTradeIns: [{}] | CountriesToPlayers: [{}] ",
                           super.toString (), currentPlayer, currentRound, cardsInHand, availableTradeIns,
                           countriesToPlayers);
  }

  @RequiredForNetworkSerialization
  private PlayerRestoreGameStateEvent ()
  {
    currentPlayer = null;
    currentRound = 0;
    cardsInHand = null;
    availableTradeIns = null;
    countriesToPlayers = null;
  }
}
