package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.core.model.TurnPhase;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface CardModel
{
  Card giveCard (final Id player, final TurnPhase turnPhase);

  CardSet getCardsInHand (final Id player);

  ImmutableSet <CardSet.Match> computeMatchesFor (final Id player);

  int tradeInCards (final Id player, final CardSet.Match tradeInCards, final TurnPhase turnPhase);

  int getNextTradeInBonus ();

  boolean hasCardWith (final String name);

  boolean isCardInHand (final Id player, final Card card);

  boolean areCardsInHand (final Id player, final CardSet cards);
}
