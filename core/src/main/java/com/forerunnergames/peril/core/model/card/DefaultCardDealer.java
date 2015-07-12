package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DefaultCardDealer implements CardDealer
{
  private static final Logger log = LoggerFactory.getLogger (DefaultCardDealer.class);

  private final ImmutableSet <Card> baseDeck;
  private final Set <Card> liveDeck = Sets.newHashSet ();
  private final Set <Card> discardPile = Sets.newHashSet ();
  @Nullable
  private Iterator <Card> liveDeckItr = null;

  DefaultCardDealer (final ImmutableSet <Card> baseDeck)
  {
    Arguments.checkIsNotNull (baseDeck, "baseDeck");

    this.baseDeck = baseDeck;

    // initial deck shuffle
    liveDeck.addAll (Randomness.shuffle (baseDeck));
    liveDeckItr = liveDeck.iterator ();
  }

  @Override
  public Card take ()
  {
    Preconditions.checkIsTrue (liveDeck.size () > 0 || discardPile.size () > 0, "No cards available!");

    if (needsReshuffle ())
    {
      reshuffle ();
    }
    final Card nextCard = liveDeckItr.next ();
    liveDeckItr.remove ();
    return nextCard;
  }

  @Override
  public void discard (final Card card)
  {
    Arguments.checkIsNotNull (card, "card");
    Arguments.checkIsTrue (baseDeck.contains (card), String.format ("%s does not exist.", card));
    Arguments.checkIsTrue (!liveDeck.contains (card), String.format ("%s is currently in the deck.", card));
    Arguments.checkIsTrue (!discardPile.contains (card), String.format ("%s has already been discarded.", card));

    discardPile.add (card);
  }

  @Override
  public void discard (final CardSet cards)
  {
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkIsTrue (baseDeck.containsAll (cards), String.format ("Unrecognized cards in [%s]", cards));
    Arguments.checkIsTrue (!liveDeck.containsAll (cards),
                           String.format ("Found one or more cards already in deck [%s]", cards));
    Arguments.checkIsTrue (!discardPile.containsAll (cards),
                           String.format ("Found one or more cards already discarded [%s]", cards));

    discardPile.addAll (cards);
  }

  @Override
  public Card cardWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    final Optional <Card> card = findCardByName (name, baseDeck);
    if (!card.isPresent ()) throw new IllegalStateException (String.format ("Could not find card with name: %s", name));
    return card.get ();
  }

  @Override
  public boolean existsCardWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return findCardByName (name, baseDeck).isPresent ();
  }

  @Override
  public boolean isInDeck (final Card card)
  {
    Arguments.checkIsNotNull (card, "card");

    return liveDeck.contains (card);
  }

  @Override
  public boolean isInDiscardPile (final Card card)
  {
    Arguments.checkIsNotNull (card, "card");

    return discardPile.contains (card);
  }

  private Optional <Card> findCardByName (final String name, final Iterable <Card> cards)
  {
    for (final Card card : cards)
    {
      if (card.hasName (name)) return Optional.of (card);
    }
    return Optional.absent ();
  }

  private boolean needsReshuffle ()
  {
    return liveDeck.isEmpty ();
  }

  private void reshuffle ()
  {
    // @formatter:off [workaround for Eclipse 4.5 formatter bug; see 470977 for status]
    assert !discardPile.isEmpty () || !liveDeck.isEmpty ();
    // @formatter:on

    // in case reshuffle is called early for any reason, add any remaining cards in liveDeck to discard pile
    // and log a warning message
    if (liveDeck.size () > 0)
    {
      log.warn ("Early call to reshuffle; moving {} cards from deck to discard pile...", liveDeck.size ());
      discardPile.addAll (liveDeck);
      liveDeck.clear ();
    }
    liveDeck.addAll (Randomness.shuffle (discardPile));
    discardPile.clear ();
    liveDeckItr = liveDeck.iterator ();
  }
}
