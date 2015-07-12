package com.forerunnergames.peril.core.model.card;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

public abstract class PlayerCardHandlerTest
{
  private final GameRules defaultRules = new ClassicGameRules.Builder ().build ();

  protected abstract PlayerCardHandler createPlayerCardHandler (final GameRules rules);

  @Test
  public void testAddCardToHandOfPlayer ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (defaultRules);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final Card card = CardFactory.create ("TestCard", CardType.TYPE1);
    cardHandler.addCardToHand (testPlayer.getId (), card);
    assertTrue (cardHandler.isCardInHand (testPlayer.getId (), card));
  }

  @Test
  public void testRemoveAllCardsFromHandOfPlayer ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (defaultRules);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final ImmutableSet.Builder <Card> cards = ImmutableSet.builder ();
    for (int i = 0; i < 3; i++)
    {
      final Card card = CardFactory.create ("TestCard-" + i, CardType.TYPE2);
      cardHandler.addCardToHand (testPlayer.getId (), card);
      cards.add (card);
    }
    final CardSet toRemove = createCardSetFrom (cards.build ());
    cardHandler.removeCardsFromHand (testPlayer.getId (), toRemove);
    assertTrue (cardHandler.getCardsInHand (testPlayer.getId ()).size () == 0);
  }

  @Test
  public void testRemoveSomeCardsFromHandOfPlayer ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (defaultRules);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final ImmutableSet.Builder <Card> cardsToRetain = ImmutableSet.builder ();
    final ImmutableSet.Builder <Card> cardsToRemove = ImmutableSet.builder ();
    for (int i = 0; i < 5; i++)
    {
      final Card card = CardFactory.create ("TestCard-" + i, CardType.TYPE3);
      cardHandler.addCardToHand (testPlayer.getId (), card);
      if (i < 2)
      {
        cardsToRetain.add (card);
      }
      else
      {
        cardsToRemove.add (card);
      }
    }

    final CardSet retainSet = createCardSetFrom (cardsToRetain.build ());
    final CardSet removeSet = createCardSetFrom (cardsToRemove.build ());
    cardHandler.removeCardsFromHand (testPlayer.getId (), removeSet);
    assertFalse (cardHandler.getCardsInHand (testPlayer.getId ()).containsAny (removeSet));
    assertTrue (cardHandler.getCardsInHand (testPlayer.getId ()).containsAll (retainSet));
  }

  @Test
  public void testIsCardInHand ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (defaultRules);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final Card card = CardFactory.create ("TestCard", CardType.TYPE1);
    cardHandler.addCardToHand (testPlayer.getId (), card);
    assertTrue (cardHandler.isCardInHand (testPlayer.getId (), card));
  }

  @Test
  public void testIsCardNotInHand ()
  {
    final Card card = CardFactory.create ("TestCard", CardType.random ());
    final PlayerCardHandler cardHandler = createPlayerCardHandler (defaultRules);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    assertFalse (cardHandler.isCardInHand (testPlayer.getId (), card));
  }

  @Test
  public void testAreCardsInHand ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (defaultRules);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final Card card1 = CardFactory.create ("TestCard-1", CardType.TYPE1);
    final Card card2 = CardFactory.create ("TestCard-2", CardType.TYPE2);
    cardHandler.addCardToHand (testPlayer.getId (), card1);
    cardHandler.addCardToHand (testPlayer.getId (), card2);
    assertTrue (cardHandler.areCardsInHand (testPlayer.getId (), createCardSetFrom (ImmutableSet.of (card1, card2))));
  }

  @Test
  public void testAreCardsNotInHand ()
  {
    final ImmutableSet <Card> cards = CardDealerTest.generateCards (CardType.random (), 3);
    final PlayerCardHandler cardHandler = createPlayerCardHandler (defaultRules);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    assertFalse (cardHandler.areCardsInHand (testPlayer.getId (), createCardSetFrom (cards)));
  }

  private CardSet createCardSetFrom (final ImmutableSet <Card> cards)
  {
    final GameRules defaultRules = new ClassicGameRules.Builder ().build ();
    return new CardSet (defaultRules, cards);
  }
}
