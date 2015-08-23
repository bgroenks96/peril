package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

final class DefaultPlayerCardHandler implements PlayerCardHandler
{
  private final BiMap <Id, CardSet> playerHands = HashBiMap.create ();
  private final GameRules rules;

  DefaultPlayerCardHandler (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  @Override
  public void addCardToHand (final Id playerId, final Card card)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    add (playerId, card);
  }

  @Override
  public void removeCardsFromHand (final Id playerId, final CardSet cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (cards, "cards");

    remove (playerId, cards);
  }

  @Override
  public CardSet getCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final Optional <CardSet> cards = get (playerId);
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit type cast
    if (!cards.isPresent ()) return newCardSet (ImmutableSet.<Card> of ());
    return cards.get ();
  }

  @Override
  public int countCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final Optional <CardSet> cards = get (playerId);
    if (!cards.isPresent ()) return 0;
    return cards.get ().size ();
  }

  @Override
  public boolean isCardInHand (final Id playerId, final Card card)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (card, "card");

    return hasCardInHand (playerId, card);
  }

  @Override
  public boolean areCardsInHand (final Id playerId, final CardSet cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (cards, "cards");

    return hasCardsInHand (playerId, cards);
  }

  private void add (final Id playerId, final Card card)
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit type cast
    final CardSet hand = (playerHands.containsKey (playerId)) ? playerHands.get (playerId)
            : newCardSet (ImmutableSet.<Card> of ());
    final CardSet newHand = hand.merge (new CardSet (rules, card));
    playerHands.forcePut (playerId, newHand);
  }

  private void remove (final Id playerId, final CardSet cards)
  {
    Preconditions.checkIsTrue (hasCardsInHand (playerId, cards),
                               String.format ("%s not in hand of %s", cards, playerId));

    final CardSet hand = playerHands.get (playerId);
    final CardSet newHand = hand.difference (cards);
    playerHands.forcePut (playerId, newHand);
  }

  private Optional <CardSet> get (final Id playerId)
  {
    return Optional.fromNullable (playerHands.get (playerId));
  }

  private boolean hasCardInHand (final Id playerId, final Card card)
  {
    if (!playerHands.containsKey (playerId)) return false;
    final CardSet hand = playerHands.get (playerId);
    return hand.contains (card);
  }

  private boolean hasCardsInHand (final Id playerId, final CardSet cards)
  {
    if (!playerHands.containsKey (playerId)) return false;
    final CardSet hand = playerHands.get (playerId);
    return hand.containsAll (cards);
  }

  private CardSet newCardSet (final ImmutableSet <Card> cards)
  {
    return new CardSet (rules, cards);
  }
}
