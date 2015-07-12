package com.forerunnergames.peril.core.model.card;

interface CardDealer
{
  Card take ();

  void discard (final Card card);

  void discard (final CardSet cards);

  Card cardWith (final String name);

  boolean existsCardWith (final String name);

  boolean isInDeck (final Card card);

  boolean isInDiscardPile (final Card card);
}
