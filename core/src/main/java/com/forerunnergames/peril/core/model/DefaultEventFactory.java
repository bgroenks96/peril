package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceInitialCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
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

final class DefaultEventFactory implements EventFactory
{
  private final PlayerModel playerModel;
  private final CountryMapGraphModel countryMapGraphModel;
  private final CountryOwnerModel countryOwnerModel;
  private final CountryArmyModel countryArmyModel;
  private final ContinentOwnerModel continentOwnerModel;
  private final CardModel cardModel;
  private final GameRules rules;

  DefaultEventFactory (final PlayerModel playerModel,
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
  public PlayerReinforceInitialCountryRequestEvent createInitialReinforcementRequestFor (final Id playerId)
  {
    final PlayerPacket player = playerModel.playerPacketWith (playerId);
    return new PlayerReinforceInitialCountryRequestEvent (player, countryOwnerModel.getCountriesOwnedBy (playerId),
            rules.getInitialReinforcementArmyCount ());
  }

  @Override
  public PlayerReinforceCountriesRequestEvent createReinforcementRequestFor (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final int countryReinforcementBonus = rules
            .calculateCountryReinforcements (countryOwnerModel.countCountriesOwnedBy (playerId));
    int continentReinforcementBonus = 0;
    final ImmutableSet <ContinentPacket> playerOwnedContinents = continentOwnerModel.getContinentsOwnedBy (playerId);
    for (final ContinentPacket cont : playerOwnedContinents)
    {
      continentReinforcementBonus += cont.getReinforcementBonus ();
    }
    final int totalReinforcementBonus = countryReinforcementBonus + continentReinforcementBonus;
    playerModel.addArmiesToHandOf (playerId, totalReinforcementBonus);

    final PlayerPacket player = playerModel.playerPacketWith (playerId);

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

    return new PlayerReinforceCountriesRequestEvent (player, validCountries, playerOwnedContinents,
            countryReinforcementBonus, continentReinforcementBonus, rules.getMaxArmiesOnCountry ());
  }

  @Override
  public PlayerTradeInCardsRequestEvent createTradeInCardsRequestFor (final Id playerId, final TurnPhase turnPhase)
  {
    final PlayerPacket player = playerModel.playerPacketWith (playerId);
    final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (playerId);
    final int cardCount = cardModel.countCardsInHand (playerId);
    final ImmutableSet <CardSetPacket> matchPackets = CardPackets.fromCardMatchSet (matches);
    return new PlayerTradeInCardsRequestEvent (player, cardModel.getNextTradeInBonus (), matchPackets,
            cardCount > rules.getMaxCardsInHand (turnPhase));
  }
}
