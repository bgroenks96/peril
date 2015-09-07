package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.card.CardSet.Match;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public final class DefaultCardModel implements CardModel
{
  private final PlayerCardHandler playerCardHandler;
  private final CardDealer cardDealer;
  private final GameRules rules;
  private int tradeInCount = 0;

  public DefaultCardModel (final GameRules rules, final ImmutableSet <Card> cardDeck)
  {
    this (rules, new DefaultPlayerCardHandler (rules), new DefaultCardDealer (cardDeck));
  }

  DefaultCardModel (final GameRules rules, final PlayerCardHandler playerCardHandler, final CardDealer cardDealer)
  {
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (playerCardHandler, "playerCardHandler");
    Arguments.checkIsNotNull (cardDealer, "cardDealer");

    this.rules = rules;
    this.playerCardHandler = playerCardHandler;
    this.cardDealer = cardDealer;
  }

  @Override
  public Card giveCard (final Id playerId, final TurnPhase turnPhase)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (turnPhase, "turnPhase");
    final int maxCardsInHand = rules.getMaxCardsInHand (turnPhase);
    Preconditions.checkIsTrue (playerCardHandler.countCardsInHand (playerId) < maxCardsInHand,
                               String.format ("Player [%s] has reached maximum cards in hand [%d] for [%s]", playerId,
                                              maxCardsInHand, turnPhase));

    final Card card = cardDealer.take ();
    playerCardHandler.addCardToHand (playerId, card);
    return card;
  }

  @Override
  public CardSet getCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerCardHandler.getCardsInHand (playerId);
  }

  @Override
  public int countCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerCardHandler.countCardsInHand (playerId);
  }

  // --- delegation methods --- //

  @Override
  public boolean isCardInHand (final Id playerId, final Card card)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (card, "card");

    return playerCardHandler.isCardInHand (playerId, card);
  }

  @Override
  public Card cardWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return cardDealer.cardWith (name);
  }

  @Override
  public boolean existsCardWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return cardDealer.existsCardWith (name);
  }

  @Override
  public ImmutableSet <Match> computeMatchesFor (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final CardSet playerHand = playerCardHandler.getCardsInHand (playerId);
    if (playerHand.size () < rules.getCardTradeInCount ()) return ImmutableSet.of ();
    final ImmutableSet <CardSet> powerSet = playerHand.powerSet ();
    final ImmutableSet.Builder <Match> matches = ImmutableSet.builder ();
    for (final CardSet cardSet : powerSet)
    {
      if (cardSet.size () != rules.getCardTradeInCount ()) continue;
      if (cardSet.isMatch ()) matches.add (cardSet.match ());
    }
    return matches.build ();
  }

  @Override
  public Result <DenialReason> requestTradeInCards (final Id playerId,
                                                    final Match tradeInCards,
                                                    final TurnPhase turnPhase)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (tradeInCards, "tradeInCards");
    Arguments.checkIsNotNull (turnPhase, "turnPhase");

    final int numCards = playerCardHandler.countCardsInHand (playerId);
    final boolean isOptionalTradeInNotAllowed = turnPhase != TurnPhase.REINFORCE
            && numCards < rules.getMinCardsInHandForTradeInReinforcePhase ();
    final boolean isRequiredTradeInNotAllowed = numCards >= rules.getMinCardsInHandToRequireTradeIn (turnPhase);
    if (isOptionalTradeInNotAllowed && isRequiredTradeInNotAllowed)
    {
      return Result.failure (DenialReason.TRADE_IN_NOT_ALLOWED);
    }

    final CardSet playerHand = playerCardHandler.getCardsInHand (playerId);
    final CardSet matchSet = tradeInCards.getCardSet ();
    if (!playerHand.containsAll (matchSet)) return Result.failure (DenialReason.CARD_NOT_IN_HAND);
    playerCardHandler.removeCardsFromHand (playerId, matchSet);
    cardDealer.discard (matchSet);
    tradeInCount++;
    return Result.success ();
  }

  @Override
  public int getNextTradeInBonus ()
  {
    return rules.calculateTradeInBonusReinforcements (tradeInCount);
  }

  @Override
  public boolean areCardsInHand (final Id playerId, final CardSet cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (cards, "cards");

    return playerCardHandler.areCardsInHand (playerId, cards);
  }

  public static ImmutableSet <Card> generateDefaultCardDeck (final int countryCount)
  {
    Arguments.checkIsNotNegative (countryCount, "countryCount");

    final ImmutableSet.Builder <Card> builder = ImmutableSet.builder ();
    for (int i = 0; i < countryCount; i++)
    {
      builder.add (CardFactory.create ("Card-" + i, CardType.random ()));
    }
    return builder.build ();
  }
}
