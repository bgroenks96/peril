/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Strings;

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
  public boolean canTake ()
  {
    return !liveDeck.isEmpty () || !discardPile.isEmpty ();
  }

  @Override
  public Card take ()
  {
    Preconditions.checkIsTrue (canTake (), "No cards available!");

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
    Arguments.checkIsTrue (baseDeck.contains (card), "{} does not exist.", card);
    Arguments.checkIsFalse (liveDeck.contains (card), "{} is currently in the deck.", card);
    Arguments.checkIsFalse (discardPile.contains (card), "{} has already been discarded.", card);

    discardPile.add (card);
  }

  @Override
  public void discard (final CardSet cards)
  {
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkIsTrue (baseDeck.containsAll (cards), "Unrecognized cards in [{}]", cards);
    Arguments.checkIsFalse (areAnyInDeck (cards), "Found one or more cards already in deck [{}]", cards);
    Arguments.checkIsFalse (areAnyInDiscardPile (cards), "Found one or more cards already discarded [{}]", cards);

    discardPile.addAll (cards);
  }

  @Override
  public void returnToDeck (final CardSet cards)
  {
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkIsTrue (baseDeck.containsAll (cards), "Unrecognized cards in [{}]", cards);
    Arguments.checkIsFalse (areAnyInDeck (cards), "Found one or more cards already in deck [{}]", cards);
    Arguments.checkIsFalse (areAnyInDiscardPile (cards), "Found one or more cards already discarded [{}]", cards);

    // Shuffles the live deck only, after adding the specified cards to it. Discard pile is unaffected.
    final ImmutableSet <Card> liveDeckCopy = ImmutableSet.<Card> builder ().addAll (liveDeck).addAll (cards).build ();
    liveDeck.clear ();
    liveDeck.addAll (Randomness.shuffle (liveDeckCopy));
    liveDeckItr = liveDeck.iterator ();
  }

  @Override
  public Card cardWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    final Optional <Card> card = findCardByName (name, baseDeck);
    if (!card.isPresent ())
      throw new IllegalStateException (Strings.format ("Could not find card with name: {}", name));
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

  @Override
  public int getDeckCount ()
  {
    return liveDeck.size ();
  }

  @Override
  public int getDiscardCount ()
  {
    return discardPile.size ();
  }

  private boolean areAnyInDeck (final CardSet cards)
  {
    for (final Card card : cards)
    {
      if (liveDeck.contains (card)) return true;
    }

    return false;
  }

  private boolean areAnyInDiscardPile (final CardSet cards)
  {
    for (final Card card : cards)
    {
      if (discardPile.contains (card)) return true;
    }

    return false;
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
    assert !discardPile.isEmpty () || !liveDeck.isEmpty ();

    // in case reshuffle is called early for any reason, add any remaining cards in liveDeck to discard pile
    // and log a warning message
    if (!liveDeck.isEmpty ())
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
