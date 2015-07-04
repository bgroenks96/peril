package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

/**
 * An immutable, set-like data structure for holding sets of cards. A CardSet can be verified as a valid trade-in match
 * through the {@link #isMatch()} method and obtained via the {@link #match()} method. Any CardSets referenced through a
 * {@link Match} object are guaranteed to be validated card sets.
 */
public final class CardSet implements Iterable <Card>
{
  private final ImmutableSet <Card> cards;
  private final GameRules rules;

  private final boolean isMatch;

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

  public boolean contains (final CardSet cardSet)
  {
    Arguments.checkIsNotNull (cardSet, "cardSet");

    return cards.contains (cardSet.cards);
  }

  public boolean containsAny (final CardSet cardSet)
  {
    Arguments.checkIsNotNull (cardSet, "cardSet");

    return Sets.intersection (cardSet.cards, cards).size () > 0;
  }

  public int size ()
  {
    return cards.size ();
  }

  @Override
  public UnmodifiableIterator <Card> iterator ()
  {
    return cards.iterator ();
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

  /**
   * Immutable view of the Cards in a validated CardSet.
   */
  public final class Match
  {
    public ImmutableSet <Card> getCards ()
    {
      return cards;
    }

    private Match ()
    {
    }
  }
}
