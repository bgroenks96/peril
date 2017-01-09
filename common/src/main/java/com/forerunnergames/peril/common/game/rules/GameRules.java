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

package com.forerunnergames.peril.common.game.rules;

import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;

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

  int getMinArmiesOnCountryForDefend ();

  int getMinArmiesOnSourceCountryForFortify ();

  int getMaxArmiesOnTargetCountryForFortify ();

  int getMinTotalPlayers ();

  int getMaxTotalPlayers ();

  int getMinTotalCountryCount ();

  int getMaxTotalCountryCount ();

  int getMinWinPercentage ();

  int getMaxWinPercentage ();

  int getMinTotalPlayerLimit ();

  int getMaxTotalPlayerLimit ();

  int getTotalPlayerLimit ();

  int getPlayerLimitFor (final PersonSentience sentience);

  int getSpectatorLimit ();

  PersonLimits getPersonLimits ();

  int getTotalCountryCount ();

  int getWinPercentage ();

  int getWinningCountryCount ();

  int getMinReinforcementsPlacedPerCountry ();

  int getCardTradeInCount ();

  int getMaxCardsInHand (final TurnPhase phase);

  int getAbsoluteMaxCardsInHand ();

  int getAbsoluteMinCardsInHand ();

  int getMinCardsInHandForTradeInReinforcePhase ();

  int getMinCardsInHandToRequireTradeIn (final TurnPhase turnPhase);

  int getAbsoluteMinAttackerDieCount ();

  int getAbsoluteMaxAttackerDieCount ();

  int getAbsoluteMinDefenderDieCount ();

  int getAbsoluteMaxDefenderDieCount ();

  DieRange getAbsoluteAttackerDieRange ();

  DieRange getAbsoluteDefenderDieRange ();

  int getMinAttackerDieCount (final int attackingCountryArmyCount);

  int getMaxAttackerDieCount (final int attackingCountryArmyCount);

  DieRange getAttackerDieRange (final int attackingCountryArmyCount);

  int getMinDefenderDieCount (final int defendingCountryArmyCount);

  int getMaxDefenderDieCount (final int defendingCountryArmyCount);

  DieRange getDefenderDieRange (final int defendingCountryArmyCount);

  int getMinOccupyArmyCount (final int attackingPlayerDieCount);

  int getMaxOccupyArmyCount (final int attackingCountryArmyCount);

  int getMinFortifyDeltaArmyCount (int sourceCountryArmyCount, int targetCountryArmyCount);

  int getMaxFortifyDeltaArmyCount (final int sourceCountryArmyCount, int targetCountryArmyCount);

  DieOutcome determineAttackerOutcome (final DieFaceValue attackerDie, final DieFaceValue defenderDie);

  DieOutcome determineDefenderOutcome (final DieFaceValue defenderDie, final DieFaceValue attackerDie);

  ImmutableList <Integer> getInitialPlayerCountryDistribution (final int playerCount);

  int calculateCountryReinforcements (final int ownedCountryCount);

  int calculateTradeInBonusReinforcements (final int globalTradeInCount);

  boolean isValidWinPercentage (final int winPercentage);

  boolean isValidCardSet (final ImmutableList <CardType> cardTypes);

  boolean canBattle (final int attackingCountryArmies, final int defendingCountryArmies);

  boolean attackerCanBattle (final int attackingCountryArmies);

  boolean defenderCanBattle (final int defendingCountryArmies);

  BattleOutcome getBattleOutcome (final int attackingCountryArmies, final int defendingCountryArmies);
}
