package com.forerunnergames.peril.common.game.rules;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.TurnPhase;

import com.google.common.collect.ImmutableList;

public interface GameRules
{
  int getInitialArmies ();

  InitialCountryAssignment getInitialCountryAssignment ();

  int getMinPlayerCountryCount ();

  int getMaxPlayerCountryCount ();

  int getMinArmiesInHand ();

  int getMaxArmiesInHand ();

  int getMinArmiesOnCountry ();

  int getMaxArmiesOnCountry ();

  int getMinArmiesOnCountryForAttack ();

  int getMinArmiesOnCountryForFortify ();

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

  int getMinTotalAttackerDieCount ();

  int getMaxTotalAttackerDieCount ();

  int getMinTotalDefenderDieCount ();

  int getMaxTotalDefenderDieCount ();

  int getMinAttackerDieCount (final int attackingCountryArmyCount);

  int getMaxAttackerDieCount (final int attackingCountryArmyCount);

  int getMinDefenderDieCount (final int defendingCountryArmyCount);

  int getMaxDefenderDieCount (final int defendingCountryArmyCount);

  int getMinOccupyArmyCount (final int attackingPlayerDieCount);

  int getMaxOccupyArmyCount (final int attackingCountryArmyCount);

  int getMaxFortifyArmyCount (final int sourceCountryArmyCount);

  DieOutcome determineAttackerOutcome (final DieFaceValue attackerDie, final DieFaceValue defenderDie);

  DieOutcome determineDefenderOutcome (final DieFaceValue defenderDie, final DieFaceValue attackerDie);

  ImmutableList <Integer> getInitialPlayerCountryDistribution (final int playerCount);

  int calculateCountryReinforcements (final int ownedCountryCount);

  int calculateTradeInBonusReinforcements (final int globalTradeInCount);

  boolean isValidWinPercentage (final int winPercentage);

  boolean isValidCardSet (final ImmutableList <CardType> cardTypes);
}
