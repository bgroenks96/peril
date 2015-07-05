package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.tools.common.id.Id;

interface PlayerCardHandler
{
  void addCardToHand (final Id playerId, final Card card);

  void removeCardsFromHand (final Id playerId, final CardSet cards);

  CardSet getCardsInHand (final Id playerId);

  int countCardsInHand (final Id playerId);

  boolean isCardInHand (final Id playerId, final Card card);

  boolean areCardsInHand (final Id playerId, final CardSet cards);
}
