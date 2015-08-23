package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.game.rules.GameRules;

import com.google.common.collect.ImmutableSet;

public class DefaultCardModelTest extends CardModelTest
{
  @Override
  protected CardModel createCardModel (final GameRules rules, final ImmutableSet <Card> cards)
  {
    return new DefaultCardModel (rules, new DefaultPlayerCardHandler (rules), new DefaultCardDealer (cards));
  }

  @Override
  protected CardModel createCardModel (final GameRules rules, final CardDealer dealer, final ImmutableSet <Card> cards)
  {
    return new DefaultCardModel (rules, new DefaultPlayerCardHandler (rules), dealer);
  }

  @Override
  protected CardDealer createCardDealer (final ImmutableSet <Card> cardDeck)
  {
    return new DefaultCardDealer (cardDeck);
  }
}
