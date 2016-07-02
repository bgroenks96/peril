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

package com.forerunnergames.peril.core.events;

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class DefaultEventFactory implements EventFactory
{
  private final PlayerModel playerModel;
  private final CountryMapGraphModel countryMapGraphModel;
  private final CountryOwnerModel countryOwnerModel;
  private final CountryArmyModel countryArmyModel;
  private final ContinentOwnerModel continentOwnerModel;
  private final CardModel cardModel;
  private final GameRules rules;

  public DefaultEventFactory (final PlayerModel playerModel,
                              final PlayMapModel playMapModel,
                              final CardModel cardModel,
                              final GameRules rules)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (cardModel, "cardModel");
    Arguments.checkIsNotNull (rules, "rules");

    this.playerModel = playerModel;
    this.cardModel = cardModel;
    this.rules = rules;

    countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    countryArmyModel = playMapModel.getCountryArmyModel ();
    continentOwnerModel = playMapModel.getContinentOwnerModel ();
  }

  @Override
  public PlayerBeginReinforcementEvent createReinforcementEventFor (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final ImmutableSet <ContinentPacket> playerOwnedContinents = continentOwnerModel.getContinentsOwnedBy (playerId);
    final ImmutableSet <CountryPacket> validCountries;
    final Predicate <CountryPacket> filter = new Predicate <CountryPacket> ()
    {
      @Override
      public boolean apply (final CountryPacket input)
      {
        return input.getArmyCount () < rules.getMaxArmiesOnCountry ();
      }
    };
    validCountries = ImmutableSet.copyOf (Sets.filter (countryOwnerModel.getCountriesOwnedBy (playerId), filter));

    final int playerArmyCount = playerModel.getArmiesInHand (playerId);

    return new PlayerBeginReinforcementEvent (playerModel.playerPacketWith (playerId), validCountries,
            playerOwnedContinents, Math.min (rules.getMaxArmiesInHand (), playerArmyCount));
  }

  @Override
  public PlayerCardTradeInAvailableEvent createCardTradeInEventFor (final Id playerId,
                                                                    final ImmutableSet <CardSet.Match> matches,
                                                                    final TurnPhase turnPhase)
  {
    final int cardCount = cardModel.countCardsInHand (playerId);
    final ImmutableSet <CardSetPacket> matchPackets = CardPackets.fromCardMatchSet (matches);
    return new PlayerCardTradeInAvailableEvent (playerModel.playerPacketWith (playerId),
            cardModel.getNextTradeInBonus (), matchPackets, cardCount > rules.getMaxCardsInHand (turnPhase));
  }
}
