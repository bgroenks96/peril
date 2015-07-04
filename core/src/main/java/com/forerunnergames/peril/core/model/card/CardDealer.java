package com.forerunnergames.peril.core.model.card;

interface CardDealer
{
  Card take ();

  void discard (final Card card);

  Card cardWith (final String name);

  boolean existsCardWith (final String name);

  boolean isInDeck (final Card card);

  boolean isInDiscardPile (final Card card);
}
