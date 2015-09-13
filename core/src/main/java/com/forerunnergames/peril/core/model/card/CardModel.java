package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountriesResponseDeniedEvent.Reason;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface CardModel
{
  Card giveCard (final Id playerId, final TurnPhase turnPhase);

  CardSet getCardsInHand (final Id playerId);

  int countCardsInHand (final Id playerId);

  boolean isCardInHand (final Id playerId, final Card card);

  Card cardWith (final String name);

  boolean existsCardWith (final String name);

  ImmutableSet <CardSet.Match> computeMatchesFor (final Id playerId);

  Result <Reason> requestTradeInCards (final Id playerId, final CardSet.Match tradeInCards, final TurnPhase turnPhase);

  int getNextTradeInBonus ();

  boolean areCardsInHand (final Id playerId, final CardSet cards);
}
