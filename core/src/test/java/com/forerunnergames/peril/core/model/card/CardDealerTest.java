package com.forerunnergames.peril.core.model.card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

public abstract class CardDealerTest
{
  private static final ImmutableSet <Card> defaultCards = generateTestCards ();

  protected abstract CardDealer createCardDealer (final ImmutableSet <Card> cards);

  @Test
  public void testIsInDeckNotInDiscardPile ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard));
    assertTrue (deckHandler.isInDeck (testCard));
    assertFalse (deckHandler.isInDiscardPile (testCard));
  }

  @Test
  public void testIsNotInDeckWhenDeckIsEmpty ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.<Card> of ());
    assertFalse (deckHandler.isInDeck (testCard));
  }

  @Test
  public void testTakeFromDeck ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard));
    assertEquals (testCard, deckHandler.take ());
    assertFalse (deckHandler.isInDeck (testCard));
  }

  @Test (expected = IllegalStateException.class)
  public void testTakeFromDeckFailsWhenCardNotInDeck ()
  {
    final CardDealer deckHandler = createCardDealer (ImmutableSet.<Card> of ());
    deckHandler.take ();
  }

  @Test
  public void testDiscardNotInDeckIsInDiscardPile ()
  {
    final CardDealer deckHandler = createCardDealer (defaultCards);
    final Card card = deckHandler.take ();
    deckHandler.discard (card);
    assertFalse (deckHandler.isInDeck (card));
    assertTrue (deckHandler.isInDiscardPile (card));
  }

  @Test
  public void testDiscardReturnsToDeckWhenEmpty ()
  {
    final Card testCard1 = CardFactory.create ("TestCard-1", CardType.TYPE1);
    final Card testCard2 = CardFactory.create ("TestCard-2", CardType.TYPE2);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard1, testCard2));
    final Card card1 = deckHandler.take ();
    final Card card2 = deckHandler.take ();
    assertTrue (deckHandler.isInDeck (card1));
    assertFalse (deckHandler.isInDeck (card2));
  }

  @Test
  public void testDiscardReturnsToDeckWhenBothEmpty ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard));
    final Card card = deckHandler.take ();
    deckHandler.discard (card);
    assertTrue (deckHandler.isInDeck (card));
  }

  @Test
  public void testCardWith ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard));
    assertEquals (testCard, deckHandler.cardWith (testCard.getName ()));
  }

  @Test (expected = IllegalStateException.class)
  public void testCardWithFailsWhenNotInDeck ()
  {
    final CardDealer deckHandler = createCardDealer (ImmutableSet.<Card> of ());
    deckHandler.cardWith ("");
  }

  static ImmutableSet <Card> generateCards (final CardType type, final int count)
  {
    final ImmutableSet.Builder <Card> cardSetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; i++)
    {
      cardSetBuilder.add (CardFactory.create ("TestCard-" + i, type));
    }
    return cardSetBuilder.build ();
  }

  static ImmutableSet <Card> generateTestCards ()
  {
    final ImmutableSet.Builder <Card> cardSetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < CardModelTest.DEFAULT_CARD_COUNT; i++)
    {
      final int type = i % 3;
      cardSetBuilder.add (CardFactory.create ("TestCard-" + i, CardType.fromValue (type)));
    }
    for (int i = 0; i < CardModelTest.DEFAULT_WILDCARD_COUNT; i++)
    {
      cardSetBuilder.add (CardFactory.create ("Test-Wildcard-" + i, CardType.WILDCARD));
    }
    return cardSetBuilder.build ();
  }

}
