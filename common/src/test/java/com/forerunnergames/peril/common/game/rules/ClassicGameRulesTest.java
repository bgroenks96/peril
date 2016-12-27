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

package com.forerunnergames.peril.common.game.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

public class ClassicGameRulesTest
{
  @Test
  public void testGetInitialArmiesForMaxPlayers ()
  {
    final GameRules rules = ClassicGameRules.builder ().maxHumanPlayers ().build ();
    final int expectedInitialArmies = 5;
    final int actualInitialArmies = rules.getInitialArmies ();

    assertEquals (expectedInitialArmies, actualInitialArmies);
  }

  @Test
  public void testGetInitialArmiesForMidMinMaxPlayers ()
  {
    final int midMinMaxPlayerLimit = ClassicGameRules.MIN_TOTAL_PLAYERS
            + (ClassicGameRules.MAX_TOTAL_PLAYERS - ClassicGameRules.MIN_TOTAL_PLAYERS) / 2;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (midMinMaxPlayerLimit).build ();
    final int expectedInitialArmies = 40 - 5 * (midMinMaxPlayerLimit - 2);
    final int actualInitialArmies = rules.getInitialArmies ();

    assertEquals (expectedInitialArmies, actualInitialArmies);
  }

  @Test
  public void testGetInitialArmiesForMinPlayers ()
  {
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (ClassicGameRules.MIN_TOTAL_PLAYERS).build ();
    final int expectedInitialArmies = 40;
    final int actualInitialArmies = rules.getInitialArmies ();

    assertEquals (expectedInitialArmies, actualInitialArmies);
  }

  @Test
  public void testGetInitialCountryAssignment ()
  {
    final InitialCountryAssignment assignment = InitialCountryAssignment.MANUAL;
    final GameRules rules = ClassicGameRules.builder ().initialCountryAssignment (assignment).build ();

    assertTrue (rules.getInitialCountryAssignment ().is (assignment));
  }

  @Test
  public void testGetMaxArmiesInHand ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MAX_ARMIES_IN_HAND, rules.getMaxArmiesInHand ());
  }

  @Test
  public void testGetMaxPlayers ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MAX_TOTAL_PLAYERS, rules.getMaxTotalPlayers ());
  }

  @Test
  public void testGetMinArmiesInHand ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MIN_ARMIES_IN_HAND, rules.getMinArmiesInHand ());
  }

  @Test
  public void testGetMinPlayers ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MIN_TOTAL_PLAYERS, rules.getMinTotalPlayers ());
  }

  @Test
  public void testGetMinWinPercentageForMinPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MIN_TOTAL_PLAYER_LIMIT;
    final int totalCountryCount = 105;
    final int expectedMinWinPercentage = 52;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (playerLimit)
            .totalCountryCount (totalCountryCount).build ();
    final int actualMinWinPercentage = rules.getMinWinPercentage ();

    assertEquals (expectedMinWinPercentage, actualMinWinPercentage);
  }

  @Test
  public void testGetMinWinPercentageForMinTotalCountryCount ()
  {
    final int playerLimit = 3;
    final int totalCountryCount = ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT;
    final int expectedMinWinPercentage = 41;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (playerLimit)
            .totalCountryCount (totalCountryCount).build ();
    final int actualMinWinPercentage = rules.getMinWinPercentage ();

    assertEquals (expectedMinWinPercentage, actualMinWinPercentage);
  }

  @Test
  public void testGetMinWinPercentageForMaxTotalCountryCount ()
  {
    final int playerLimit = 7;
    final int totalCountryCount = ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT;
    final int expectedMinWinPercentage = 16;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (playerLimit)
            .totalCountryCount (totalCountryCount).build ();
    final int actualMinWinPercentage = rules.getMinWinPercentage ();

    assertEquals (expectedMinWinPercentage, actualMinWinPercentage);
  }

  @Test
  public void testGetMinWinPercentageForMaxPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT;
    final int totalCountryCount = 11;
    final int expectedMinWinPercentage = 20;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (playerLimit)
            .totalCountryCount (totalCountryCount).build ();
    final int actualMinWinPercentage = rules.getMinWinPercentage ();

    assertEquals (expectedMinWinPercentage, actualMinWinPercentage);
  }

  @Test
  public void testGetMinPlayerLimit ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MIN_TOTAL_PLAYER_LIMIT, rules.getMinTotalPlayerLimit ());
  }

  @Test
  public void testGetMaxTotalCountryCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT, rules.getMaxTotalCountryCount ());
  }

  @Test
  public void testGetMinTotalCountryCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT, rules.getMinTotalCountryCount ());
  }

  @Test
  public void testGetMaxPlayerLimit ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT, rules.getMaxTotalPlayerLimit ());
  }

  @Test
  public void testGetMaxWinPercentage ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.MAX_WIN_PERCENTAGE, rules.getMaxWinPercentage ());
  }

  @Test
  public void testGetPlayerLimitForMaxPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (playerLimit).build ();

    assertEquals (playerLimit, rules.getTotalPlayerLimit ());
  }

  @Test
  public void testGetPlayerLimitForMinPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MIN_TOTAL_PLAYER_LIMIT;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (playerLimit).build ();

    assertEquals (playerLimit, rules.getTotalPlayerLimit ());
  }

  @Test
  public void testGetTotalCountryCountForMaxCountryCount ()
  {
    final int totalCountryCount = ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT;
    final GameRules rules = ClassicGameRules.builder ().totalCountryCount (totalCountryCount).build ();

    assertEquals (totalCountryCount, rules.getTotalCountryCount ());
  }

  @Test
  public void testGetTotalCountryCountForMinCountryCount ()
  {
    final int totalCountryCount = ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT;
    final GameRules rules = ClassicGameRules.builder ().totalCountryCount (totalCountryCount).build ();

    assertEquals (totalCountryCount, rules.getTotalCountryCount ());
  }

  @Test
  public void testGetWinPercentageForMaxWinPercentage ()
  {
    final int winPercentage = ClassicGameRules.MAX_WIN_PERCENTAGE;
    final GameRules rules = ClassicGameRules.builder ().winPercentage (winPercentage).build ();

    assertEquals (winPercentage, rules.getWinPercentage ());
  }

  @Test
  public void testGetWinningCountryCount ()
  {
    final int winPercentage = 87;
    final int totalCountryCount = 52;
    final int expectedWinningCountryCount = 46;
    final GameRules rules = ClassicGameRules.builder ().winPercentage (winPercentage)
            .totalCountryCount (totalCountryCount).build ();
    final int actualWinningCountryCount = rules.getWinningCountryCount ();

    assertEquals (expectedWinningCountryCount, actualWinningCountryCount);
  }

  @Test
  public void testIsValidWinPercentage ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    final int minWinPercentage = rules.getMinWinPercentage ();
    final int maxWinPercentage = rules.getMaxWinPercentage ();

    assertTrue (rules.isValidWinPercentage (minWinPercentage));
    assertTrue (rules.isValidWinPercentage (minWinPercentage));
    assertFalse (rules.isValidWinPercentage (minWinPercentage - 1));
    assertFalse (rules.isValidWinPercentage (0));
    assertFalse (rules.isValidWinPercentage (maxWinPercentage + 1));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testIllegalWinPercentage ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    rules.isValidWinPercentage (Integer.MIN_VALUE);
  }

  @Test
  public void testGetInitialPlayerCountryDistributionUniformMaxPlayers ()
  {
    final Integer expectedUniformDistributionValue = 3;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (ClassicGameRules.MAX_TOTAL_PLAYERS)
            .totalCountryCount (ClassicGameRules.MAX_TOTAL_PLAYERS * expectedUniformDistributionValue).build ();

    final ImmutableList <Integer> testDistribution = rules
            .getInitialPlayerCountryDistribution (ClassicGameRules.MAX_TOTAL_PLAYERS);
    for (final Integer countryCount : testDistribution)
    {
      assertEquals (countryCount, expectedUniformDistributionValue);
    }
  }

  @Test
  public void testGetInitialPlayerCountryDistributionNonUniformMaxPlayers ()
  {
    final int testPlayerCount = ClassicGameRules.MAX_TOTAL_PLAYERS;
    final int expectedBaseDistributionValue = 3, expectedRemainderValue = testPlayerCount - 3;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (testPlayerCount)
            .totalCountryCount (testPlayerCount * expectedBaseDistributionValue + expectedRemainderValue).build ();

    final ImmutableList <Integer> testDistribution = rules.getInitialPlayerCountryDistribution (testPlayerCount);
    for (int i = 0; i < expectedRemainderValue; i++)
    {
      assertTrue (expectedBaseDistributionValue + 1 == testDistribution.get (i));
    }
    for (int i = expectedRemainderValue; i < testPlayerCount; ++i)
    {
      assertTrue (expectedBaseDistributionValue == testDistribution.get (i));
    }
  }

  @Test
  public void testGetInitialPlayerCountryDistributionNonUniformMinPlayers ()
  {
    final int testPlayerCount = ClassicGameRules.MIN_TOTAL_PLAYERS;
    final int expectedBaseDistributionValue = 10, expectedRemainderValue = 1;
    final GameRules rules = ClassicGameRules.builder ().humanPlayerLimit (testPlayerCount)
            .totalCountryCount (testPlayerCount * expectedBaseDistributionValue + expectedRemainderValue).build ();

    final ImmutableList <Integer> testDistribution = rules.getInitialPlayerCountryDistribution (testPlayerCount);
    for (int i = 0; i < expectedRemainderValue; ++i)
    {
      assertTrue (expectedBaseDistributionValue + 1 == testDistribution.get (i));
    }
    for (int i = expectedRemainderValue; i < testPlayerCount; ++i)
    {
      assertTrue (expectedBaseDistributionValue == testDistribution.get (i));
    }
  }

  @Test
  public void testIsValidCardSetAllMatching ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.TYPE1, CardType.TYPE1, CardType.TYPE1)));
  }

  @Test
  public void testIsValidCardSetAllUnique ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.TYPE3, CardType.TYPE1, CardType.TYPE2)));
  }

  @Test
  public void testIsValidCardSetUniqueWithWildcard ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.WILDCARD, CardType.TYPE2, CardType.TYPE3)));
  }

  @Test
  public void testIsValidCardSetMatchingWithWildcard ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.WILDCARD, CardType.TYPE2, CardType.TYPE2)));
  }

  @Test
  public void testIsValidCardSetTwoWildcards ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.WILDCARD, CardType.WILDCARD, CardType.TYPE3)));
  }

  @Test
  public void testIsValidCardSetAllWildcards ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.WILDCARD, CardType.WILDCARD, CardType.WILDCARD)));
  }

  @Test
  public void testNotValidCardSetMismatchOrder1 ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertFalse (rules.isValidCardSet (ImmutableList.of (CardType.TYPE1, CardType.TYPE1, CardType.TYPE2)));
  }

  @Test
  public void testNotValidCardSetMismatchOrder2 ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertFalse (rules.isValidCardSet (ImmutableList.of (CardType.TYPE2, CardType.TYPE1, CardType.TYPE2)));
  }

  @Test
  public void testNotValidCardSetMismatchOrder3 ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertFalse (rules.isValidCardSet (ImmutableList.of (CardType.TYPE3, CardType.TYPE1, CardType.TYPE1)));
  }

  public void testCalculateCountryReinforcementsMultipleOfThree ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertEquals (4, rules.calculateCountryReinforcements (12));
  }

  public void testCalculateCountryReinforcementsWithRemainder_v1 ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertEquals (4, rules.calculateCountryReinforcements (14));
  }

  public void testCalculateCountryReinforcementsWithRemainder_v2 ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertEquals (6, rules.calculateCountryReinforcements (20));
  }

  public void testCalculateCountryReinforcementsForOneCountryOwned ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertEquals (3, rules.calculateCountryReinforcements (1));
  }

  public void testCalculateCountryReinforcementsForFiveCountriesOwned ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    assertEquals (3, rules.calculateCountryReinforcements (5));
  }

  @Test
  public void testCalculateTradeInBonusReinforcementsZeroToFive ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    int expected = 4; // 4 at first trade in
    for (int i = 0; i < 5; i++, expected += 2)
    {
      assertEquals (expected, rules.calculateTradeInBonusReinforcements (i));
    }
  }

  @Test
  public void testCalculateTradeInBonusReinforcementsAfterFive ()
  {
    final GameRules rules = ClassicGameRules.defaults ();
    int expected = 15; // 15 after fifth trade in
    for (int i = 5; i < 100; i++, expected += 5)
    {
      assertEquals (expected, rules.calculateTradeInBonusReinforcements (i));
    }
  }

  @Test
  public void testGetAbsoluteMinAttackerDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MIN_ATTACKER_DIE_COUNT, rules.getAbsoluteMinAttackerDieCount ());
  }

  @Test
  public void testGetAbsoluteMaxAttackerDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MAX_ATTACKER_DIE_COUNT, rules.getAbsoluteMaxAttackerDieCount ());
  }

  @Test
  public void testGetAbsoluteMinDefenderDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MIN_DEFENDER_DIE_COUNT, rules.getAbsoluteMinDefenderDieCount ());
  }

  @Test
  public void testGetAbsoluteMaxDefenderDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MAX_DEFENDER_DIE_COUNT, rules.getAbsoluteMaxDefenderDieCount ());
  }

  @Test
  public void testGetAbsoluteAttackerDieRange ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_ATTACKER_DIE_RANGE, rules.getAbsoluteAttackerDieRange ());
  }

  @Test
  public void testGetAbsoluteDefenderDieRange ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_DEFENDER_DIE_RANGE, rules.getAbsoluteDefenderDieRange ());
  }

  @Test
  public void testGetMinAttackerDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MIN_ATTACKER_DIE_COUNT, rules.getMinAttackerDieCount (Integer.MAX_VALUE));
    assertEquals (ClassicGameRules.ABSOLUTE_MIN_ATTACKER_DIE_COUNT, rules.getMinAttackerDieCount (4));
    assertEquals (ClassicGameRules.ABSOLUTE_MIN_ATTACKER_DIE_COUNT, rules.getMinAttackerDieCount (3));
    assertEquals (ClassicGameRules.ABSOLUTE_MIN_ATTACKER_DIE_COUNT, rules.getMinAttackerDieCount (2));
    assertEquals (0, rules.getMinAttackerDieCount (1));
    assertEquals (0, rules.getMinAttackerDieCount (0));
  }

  @Test
  public void testGetMinDefenderDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MIN_DEFENDER_DIE_COUNT, rules.getMinDefenderDieCount (Integer.MAX_VALUE));
    assertEquals (ClassicGameRules.ABSOLUTE_MIN_DEFENDER_DIE_COUNT, rules.getMinDefenderDieCount (2));
    assertEquals (ClassicGameRules.ABSOLUTE_MIN_DEFENDER_DIE_COUNT, rules.getMinDefenderDieCount (1));
    assertEquals (0, rules.getMinDefenderDieCount (0));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMinAttackerDieCountThrowsExceptionNegativeAttackingCountryArmyCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMinAttackerDieCount (-1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMinAttackerDieCountThrowsExceptionNegativeAttackingCountryArmyCountIntegerMinValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMinAttackerDieCount (Integer.MIN_VALUE);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMinDefenderDieCountThrowsExceptionNegativeDefendingCountryArmyCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMinDefenderDieCount (-1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMinDefenderDieCountThrowsExceptionNegativeDefendingCountryArmyCountIntegerMinValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMinDefenderDieCount (Integer.MIN_VALUE);
  }

  @Test
  public void testGetMaxAttackerDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MAX_ATTACKER_DIE_COUNT, rules.getMaxAttackerDieCount (Integer.MAX_VALUE));
    assertEquals (ClassicGameRules.ABSOLUTE_MAX_ATTACKER_DIE_COUNT, rules.getMaxAttackerDieCount (4));
    assertEquals (2, rules.getMaxAttackerDieCount (3));
    assertEquals (1, rules.getMaxAttackerDieCount (2));
    assertEquals (0, rules.getMaxAttackerDieCount (1));
    assertEquals (0, rules.getMaxAttackerDieCount (0));
  }

  @Test
  public void testGetMaxDefenderDieCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_MAX_DEFENDER_DIE_COUNT, rules.getMaxDefenderDieCount (Integer.MAX_VALUE));
    assertEquals (ClassicGameRules.ABSOLUTE_MAX_DEFENDER_DIE_COUNT, rules.getMaxDefenderDieCount (2));
    assertEquals (1, rules.getMaxDefenderDieCount (1));
    assertEquals (0, rules.getMaxDefenderDieCount (0));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMaxAttackerDieCountThrowsExceptionNegativeAttackingCountryArmyCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMaxAttackerDieCount (-1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMaxAttackerDieCountThrowsExceptionNegativeAttackingCountryArmyCountIntegerMinValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMaxAttackerDieCount (Integer.MIN_VALUE);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMaxDefenderDieCountThrowsExceptionNegativeDefendingCountryArmyCount ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMaxDefenderDieCount (-1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testGetMaxDefenderDieCountThrowsExceptionNegativeDefendingCountryArmyCountIntegerMinValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.getMaxDefenderDieCount (Integer.MIN_VALUE);
  }

  @Test
  public void testGetAttackerDieRange ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_ATTACKER_DIE_RANGE, rules.getAttackerDieRange (Integer.MAX_VALUE));
    assertEquals (ClassicGameRules.ABSOLUTE_ATTACKER_DIE_RANGE, rules.getAttackerDieRange (4));
    assertEquals (new DieRange (1, 2), rules.getAttackerDieRange (3));
    assertEquals (new DieRange (1, 1), rules.getAttackerDieRange (2));
    assertEquals (new DieRange (0, 0), rules.getAttackerDieRange (1));
    assertEquals (new DieRange (0, 0), rules.getAttackerDieRange (0));
  }

  @Test
  public void testGetDefenderDieRange ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (ClassicGameRules.ABSOLUTE_DEFENDER_DIE_RANGE, rules.getDefenderDieRange (Integer.MAX_VALUE));
    assertEquals (ClassicGameRules.ABSOLUTE_DEFENDER_DIE_RANGE, rules.getDefenderDieRange (2));
    assertEquals (new DieRange (1, 1), rules.getDefenderDieRange (1));
    assertEquals (new DieRange (0, 0), rules.getDefenderDieRange (0));
  }

  @Test
  public void testGetMinOccupyArmyCountForAllPossibleDieCounts ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    final int attackingArmyCount = rules.getMaxArmiesOnCountry ();

    for (int i = rules.getAbsoluteMinAttackerDieCount (); i <= rules.getMaxAttackerDieCount (attackingArmyCount); i++)
    {
      assertEquals (i, rules.getMinOccupyArmyCount (i));
    }
  }

  @Test
  public void testGetMaxOccupyArmyCountForMaxArmies ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    final int attackingArmyCount = rules.getMaxArmiesOnCountry ();

    assertEquals (attackingArmyCount - 1, rules.getMaxOccupyArmyCount (attackingArmyCount));
  }

  @Test
  public void testGetMaxOccupyArmyCountForMinArmies ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    final int attackingArmyCount = rules.getMinArmiesOnCountryForAttack ();

    assertEquals (attackingArmyCount - 1, rules.getMaxOccupyArmyCount (attackingArmyCount));
  }

  @Test
  public void testDetermineAttackerOutcomeWin ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.SIX, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.FIVE, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.FOUR, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.THREE, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.TWO, DieFaceValue.ONE));

    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.SIX, DieFaceValue.TWO));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.FIVE, DieFaceValue.TWO));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.FOUR, DieFaceValue.TWO));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.THREE, DieFaceValue.TWO));

    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.SIX, DieFaceValue.THREE));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.FIVE, DieFaceValue.THREE));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.FOUR, DieFaceValue.THREE));

    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.SIX, DieFaceValue.FOUR));
    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.FIVE, DieFaceValue.FOUR));

    assertEquals (DieOutcome.WIN, rules.determineAttackerOutcome (DieFaceValue.SIX, DieFaceValue.FIVE));
  }

  @Test
  public void testDetermineAttackerOutcomeLose ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.SIX, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.FIVE, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.FOUR, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.THREE, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.TWO, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.ONE, DieFaceValue.SIX));

    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.FIVE, DieFaceValue.FIVE));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.FOUR, DieFaceValue.FIVE));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.THREE, DieFaceValue.FIVE));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.TWO, DieFaceValue.FIVE));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.ONE, DieFaceValue.FIVE));

    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.FOUR, DieFaceValue.FOUR));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.THREE, DieFaceValue.FOUR));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.TWO, DieFaceValue.FOUR));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.ONE, DieFaceValue.FOUR));

    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.THREE, DieFaceValue.THREE));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.TWO, DieFaceValue.THREE));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.ONE, DieFaceValue.THREE));

    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.TWO, DieFaceValue.TWO));
    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.ONE, DieFaceValue.TWO));

    assertEquals (DieOutcome.LOSE, rules.determineAttackerOutcome (DieFaceValue.ONE, DieFaceValue.ONE));
  }

  @Test
  public void testDetermineDefenderOutcomeWin ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.SIX, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FIVE, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FOUR, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.THREE, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.TWO, DieFaceValue.ONE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.ONE, DieFaceValue.ONE));

    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.SIX, DieFaceValue.TWO));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FIVE, DieFaceValue.TWO));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FOUR, DieFaceValue.TWO));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.THREE, DieFaceValue.TWO));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.TWO, DieFaceValue.TWO));

    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.SIX, DieFaceValue.THREE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FIVE, DieFaceValue.THREE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FOUR, DieFaceValue.THREE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.THREE, DieFaceValue.THREE));

    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.SIX, DieFaceValue.FOUR));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FIVE, DieFaceValue.FOUR));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FOUR, DieFaceValue.FOUR));

    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.SIX, DieFaceValue.FIVE));
    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.FIVE, DieFaceValue.FIVE));

    assertEquals (DieOutcome.WIN, rules.determineDefenderOutcome (DieFaceValue.SIX, DieFaceValue.SIX));
  }

  @Test
  public void testDetermineDefenderOutcomeLose ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.FIVE, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.FOUR, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.THREE, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.TWO, DieFaceValue.SIX));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.ONE, DieFaceValue.SIX));

    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.FOUR, DieFaceValue.FIVE));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.THREE, DieFaceValue.FIVE));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.TWO, DieFaceValue.FIVE));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.ONE, DieFaceValue.FIVE));

    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.THREE, DieFaceValue.FOUR));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.TWO, DieFaceValue.FOUR));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.ONE, DieFaceValue.FOUR));

    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.TWO, DieFaceValue.THREE));
    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.ONE, DieFaceValue.THREE));

    assertEquals (DieOutcome.LOSE, rules.determineDefenderOutcome (DieFaceValue.ONE, DieFaceValue.TWO));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testDetermineAttackerOutcomeNullAttackerDieFaceValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.determineAttackerOutcome (null, DieFaceValue.ONE);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testDetermineAttackerOutcomeNullDefenderDieFaceValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.determineAttackerOutcome (DieFaceValue.FOUR, null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testDetermineAttackerOutcomeNullAttackerAndDefenderDieFaceValues ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.determineAttackerOutcome (null, null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testDetermineDefenderOutcomeNullDefenderDieFaceValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.determineDefenderOutcome (null, DieFaceValue.TWO);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testDetermineDefenderOutcomeNullAttackerDieFaceValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.determineDefenderOutcome (DieFaceValue.THREE, null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testDetermineDefenderOutcomeNullDefenderAndAttackerDieFaceValues ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    rules.determineDefenderOutcome (null, null);
  }

  @Test
  public void testAttackerCanBattleTrueMinArmiesRequiredForAttack ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertTrue (rules.attackerCanBattle (ClassicGameRules.MIN_ARMIES_ON_COUNTRY_FOR_ATTACK));
  }

  @Test
  public void testAttackerCanBattleTrueOneMoreThanMinArmiesRequiredForAttack ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertTrue (rules.attackerCanBattle (ClassicGameRules.MIN_ARMIES_ON_COUNTRY_FOR_ATTACK + 1));
  }

  @Test
  public void testAttackerCanBattleTrueIntegerMaxValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertTrue (rules.attackerCanBattle (Integer.MAX_VALUE));
  }

  @Test
  public void testAttackerCanBattleFalseOneLessThanMinArmiesRequiredForAttack ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.attackerCanBattle (ClassicGameRules.MIN_ARMIES_ON_COUNTRY_FOR_ATTACK - 1));
  }

  @Test
  public void testAttackerCanBattleFalseZero ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.attackerCanBattle (0));
  }

  @Test
  public void testAttackerCanBattleFalseNegative ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.attackerCanBattle (-1));
  }

  @Test
  public void testAttackerCanBattleFalseIntegerMinValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.attackerCanBattle (Integer.MIN_VALUE));
  }

  @Test
  public void testDefenderCanBattleTrueMinArmiesRequiredForDefend ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertTrue (rules.defenderCanBattle (ClassicGameRules.MIN_ARMIES_ON_COUNTRY_FOR_DEFEND));
  }

  @Test
  public void testDefenderCanBattleTrueOneMoreThanMinArmiesRequiredForDefend ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertTrue (rules.defenderCanBattle (ClassicGameRules.MIN_ARMIES_ON_COUNTRY_FOR_DEFEND + 1));
  }

  @Test
  public void testDefenderCanBattleTrueIntegerMaxValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertTrue (rules.defenderCanBattle (Integer.MAX_VALUE));
  }

  @Test
  public void testDefenderCanBattleFalseOneLessThanMinArmiesRequiredForDefend ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.defenderCanBattle (ClassicGameRules.MIN_ARMIES_ON_COUNTRY_FOR_DEFEND - 1));
  }

  @Test
  public void testDefenderCanBattleFalseZero ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.defenderCanBattle (0));
  }

  @Test
  public void testDefenderCanBattleFalseNegative ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.defenderCanBattle (-1));
  }

  @Test
  public void testDefenderCanBattleFalseIntegerMinValue ()
  {
    final GameRules rules = ClassicGameRules.defaults ();

    assertFalse (rules.defenderCanBattle (Integer.MIN_VALUE));
  }
}
