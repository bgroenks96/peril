package com.forerunnergames.peril.core.model.rules;

import com.forerunnergames.peril.core.model.TurnPhase;
import com.forerunnergames.peril.core.model.card.CardType;
import com.forerunnergames.peril.core.shared.game.InitialCountryAssignment;

import com.google.common.collect.ImmutableList;

public interface GameRules
{
  int getInitialArmies ();

  InitialCountryAssignment getInitialCountryAssignment ();

  int getMinArmiesInHand ();

  int getMaxArmiesInHand ();

  int getMinPlayerLimit ();

  int getMaxPlayerLimit ();

  int getMinPlayers ();

  int getMaxPlayers ();

  int getMinTotalCountryCount ();

  int getMaxTotalCountryCount ();

  int getMinWinPercentage ();

  int getMaxWinPercentage ();

  int getPlayerLimit ();

  int getTotalCountryCount ();

  int getWinPercentage ();

  int getWinningCountryCount ();

  int getCardTradeInCount ();

  int getMaxCardsInHand (final TurnPhase phase);

  int getMinCardsInHandForTradeInReinforcePhase ();

  int getMinCardsInHandToRequireTradeIn (final TurnPhase turnPhase);

  ImmutableList <Integer> getInitialPlayerCountryDistribution (final int playerCount);

  int calculateCountryReinforcements (final int ownedCountryCount);

  int calculateTradeInBonusReinforcements (final int globalTradeInCount);

  boolean isValidWinPercentage (final int winPercentage);

  boolean isValidCardSet (final ImmutableList <CardType> cardTypes);
}
