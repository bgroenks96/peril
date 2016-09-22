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

package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Collection;
import java.util.Set;

/**
 * An immutable, set-like data structure for holding sets of cards. A CardSet can be verified as a valid trade-in match
 * through the {@link #isMatch()} method and obtained via the {@link #match()} method. Any CardSets referenced through a
 * {@link Match} object are guaranteed to be validated card sets.
 */
public final class CardSet implements Collection <Card>
{
  private final ImmutableSet <Card> cards;
  private final GameRules rules;

  private final boolean isMatch;

  public CardSet (final GameRules rules, final Card... cards)
  {
    this (rules, ImmutableSet.copyOf (cards));
  }

  /**
   * Creates this CardTuple with the given rules and cards.
   */
  public CardSet (final GameRules rules, final ImmutableSet <Card> cards)
  {
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkHasNoNullElements (cards, "cards");

    this.rules = rules;
    this.cards = cards;

    isMatch = validateMatch ();
  }

  @Override
  public int size ()
  {
    return cards.size ();
  }

  @Override
  public boolean isEmpty ()
  {
    return cards.isEmpty ();
  }

  @Override
  public boolean contains (final Object card)
  {
    Arguments.checkIsNotNull (card, "card");

    return cards.contains (card);
  }

  @Override
  public UnmodifiableIterator <Card> iterator ()
  {
    return cards.iterator ();
  }

  @Override
  public Object[] toArray ()
  {
    return cards.toArray ();
  }

  @Override
  public <T> T[] toArray (final T[] a)
  {
    Arguments.checkIsNotNull (a, "a");

    return cards.toArray (a);
  }

  @Override
  @Deprecated
  public boolean add (final Card e) throws UnsupportedOperationException
  {
    Arguments.checkIsNotNull (e, "e");

    return cards.add (e);
  }

  @Override
  @Deprecated
  public boolean remove (final Object o) throws UnsupportedOperationException
  {
    Arguments.checkIsNotNull (o, "o");

    return cards.remove (o);
  }

  // --- delegated methods --- //

  @Override
  public boolean containsAll (final Collection <?> cardSet)
  {
    Arguments.checkIsNotNull (cardSet, "cardSet");

    return cards.containsAll (cardSet);
  }

  @Override
  @Deprecated
  public boolean addAll (final Collection <? extends Card> c) throws UnsupportedOperationException
  {
    Arguments.checkIsNotNull (c, "c");

    return cards.addAll (c);
  }

  @Override
  @Deprecated
  public boolean removeAll (final Collection <?> c) throws UnsupportedOperationException
  {
    Arguments.checkIsNotNull (c, "c");

    return cards.removeAll (c);
  }

  @Override
  @Deprecated
  public boolean retainAll (final Collection <?> c) throws UnsupportedOperationException
  {
    Arguments.checkIsNotNull (c, "c");

    return cards.retainAll (c);
  }

  @Override
  @Deprecated
  public void clear () throws UnsupportedOperationException
  {
    cards.clear ();
  }

  /**
   * A CardSet is a tradeable match if and only if: 1) The size of the CardSet is equal to the value returned by the
   * trade-in count value specified by GameRules. 2) The combination of card types is verified as a match by GameRules.
   *
   * @return true if this CardSet is a valid, tradeable match; false otherwise.
   */
  public boolean isMatch ()
  {
    return isMatch;
  }

  public Match match ()
  {
    Preconditions.checkIsTrue (isMatch (), "Not a valid matching set.");

    return new Match ();
  }

  /**
   * Merges this CardSet with the given CardSet and returns the result. Both CardSets must have matching GameRules
   * implementations. The resulting merged CardSet is the union of the two sets.
   */
  public CardSet merge (final CardSet cardSet)
  {
    Arguments.checkIsNotNull (cardSet, "cardSet");
    Arguments.checkIsTrue (rules.getClass ().equals (cardSet.rules.getClass ()),
                           String.format ("%s rules must match.", getClass ().getSimpleName ()));

    return new CardSet (rules, Sets.union (cardSet.cards, cards).immutableCopy ());
  }

  // --- unsupported collection methods --- //

  public CardSet difference (final CardSet cardSet)
  {
    Arguments.checkIsNotNull (cardSet, "cardSet");

    return new CardSet (rules, Sets.difference (cards, cardSet.cards).immutableCopy ());
  }

  public ImmutableSet <CardSet> powerSet ()
  {
    final Set <Set <Card>> powerSet = Sets.powerSet (cards);
    final ImmutableSet.Builder <CardSet> builder = ImmutableSet.builder ();
    for (final Set <Card> set : powerSet)
    {
      builder.add (new CardSet (rules, ImmutableSet.copyOf (set)));
    }
    return builder.build ();
  }

  public boolean containsAny (final CardSet cardSet)
  {
    Arguments.checkIsNotNull (cardSet, "cardSet");

    return Sets.intersection (cardSet.cards, cards).size () > 0;
  }

  private boolean validateMatch ()
  {
    if (cards.size () != rules.getCardTradeInCount ()) return false;

    final ImmutableList.Builder <CardType> typeSetBuilder = ImmutableList.builder ();
    for (final Card card : cards)
    {
      typeSetBuilder.add (card.getType ());
    }
    return rules.isValidCardSet (typeSetBuilder.build ());
  }

  @Override
  public String toString ()
  {
    return cards.toString ();
  }

  /**
   * Immutable view of a validated CardSet.
   */
  public final class Match
  {
    public ImmutableSet <Card> getCards ()
    {
      return cards;
    }

    public CardSet getCardSet ()
    {
      return CardSet.this;
    }

    private Match ()
    {
    }
  }
}
