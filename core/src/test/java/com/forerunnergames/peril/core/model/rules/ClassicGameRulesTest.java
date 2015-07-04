package com.forerunnergames.peril.core.model.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.card.CardType;
import com.forerunnergames.peril.core.model.map.PlayMapModelTest;
import com.forerunnergames.peril.core.model.map.country.Country;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;

public class ClassicGameRulesTest
{
  @Test
  public void testGetInitialArmiesForMaxPlayers ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS).build ();
    final int expectedInitialArmies = 5;
    final int actualInitialArmies = rules.getInitialArmies ();

    assertEquals (expectedInitialArmies, actualInitialArmies);
  }

  @Test
  public void testGetInitialArmiesForMidMinMaxPlayers ()
  {
    final int midMinMaxPlayerLimit = ClassicGameRules.MIN_PLAYERS
            + (ClassicGameRules.MAX_PLAYERS - ClassicGameRules.MIN_PLAYERS) / 2;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (midMinMaxPlayerLimit).build ();
    final int expectedInitialArmies = 40 - 5 * (midMinMaxPlayerLimit - 2);
    final int actualInitialArmies = rules.getInitialArmies ();

    assertEquals (expectedInitialArmies, actualInitialArmies);
  }

  @Test
  public void testGetInitialArmiesForMinPlayers ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MIN_PLAYERS).build ();
    final int expectedInitialArmies = 40;
    final int actualInitialArmies = rules.getInitialArmies ();

    assertEquals (expectedInitialArmies, actualInitialArmies);
  }

  @Test
  public void testGetInitialCountryAssignment ()
  {
    final InitialCountryAssignment assignment = InitialCountryAssignment.MANUAL;
    final GameRules rules = new ClassicGameRules.Builder ().initialCountryAssignment (assignment).build ();

    assertTrue (rules.getInitialCountryAssignment ().is (assignment));
  }

  @Test
  public void testGetMaxArmiesInHand ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMaxArmiesInHand (), ClassicGameRules.MAX_ARMIES_IN_HAND);
  }

  @Test
  public void testGetMaxPlayers ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMaxPlayers (), ClassicGameRules.MAX_PLAYERS);
  }

  @Test
  public void testGetMinArmiesInHand ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMinArmiesInHand (), ClassicGameRules.MIN_ARMIES_IN_HAND);
  }

  @Test
  public void testGetMinPlayers ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMinPlayers (), ClassicGameRules.MIN_PLAYERS);
  }

  @Test
  public void testGetMinWinPercentageForMinPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MIN_PLAYER_LIMIT;
    final int totalCountryCount = 105;
    final int expectedMinWinPercentage = 52;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit)
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
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit)
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
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit)
            .totalCountryCount (totalCountryCount).build ();
    final int actualMinWinPercentage = rules.getMinWinPercentage ();

    assertEquals (expectedMinWinPercentage, actualMinWinPercentage);
  }

  @Test
  public void testGetMinWinPercentageForMaxPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MAX_PLAYER_LIMIT;
    final int totalCountryCount = 11;
    final int expectedMinWinPercentage = 20;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit)
            .totalCountryCount (totalCountryCount).build ();
    final int actualMinWinPercentage = rules.getMinWinPercentage ();

    assertEquals (expectedMinWinPercentage, actualMinWinPercentage);
  }

  @Test
  public void testGetMinPlayerLimit ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMinPlayerLimit (), ClassicGameRules.MIN_PLAYER_LIMIT);
  }

  @Test
  public void testGetMaxTotalCountryCount ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMaxTotalCountryCount (), ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT);
  }

  @Test
  public void testGetMinTotalCountryCount ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMinTotalCountryCount (), ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT);
  }

  @Test
  public void testGetMaxPlayerLimit ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMaxPlayerLimit (), ClassicGameRules.MAX_PLAYER_LIMIT);
  }

  @Test
  public void testGetMaxWinPercentage ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertEquals (rules.getMaxWinPercentage (), ClassicGameRules.MAX_WIN_PERCENTAGE);
  }

  @Test
  public void testGetPlayerLimitForMaxPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MAX_PLAYER_LIMIT;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit).build ();

    assertEquals (rules.getPlayerLimit (), playerLimit);
  }

  @Test
  public void testGetPlayerLimitForMinPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MIN_PLAYER_LIMIT;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit).build ();

    assertEquals (rules.getPlayerLimit (), playerLimit);
  }

  @Test
  public void testGetTotalCountryCountForMaxCountryCount ()
  {
    final int totalCountryCount = ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT;
    final GameRules rules = new ClassicGameRules.Builder ().totalCountryCount (totalCountryCount).build ();

    assertEquals (rules.getTotalCountryCount (), totalCountryCount);
  }

  @Test
  public void testGetTotalCountryCountForMinCountryCount ()
  {
    final int totalCountryCount = ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT;
    final GameRules rules = new ClassicGameRules.Builder ().totalCountryCount (totalCountryCount).build ();

    assertEquals (rules.getTotalCountryCount (), totalCountryCount);
  }

  @Test
  public void testGetWinPercentageForMaxWinPercentage ()
  {
    final int winPercentage = ClassicGameRules.MAX_WIN_PERCENTAGE;
    final GameRules rules = new ClassicGameRules.Builder ().winPercentage (winPercentage).build ();

    assertEquals (rules.getWinPercentage (), winPercentage);
  }

  @Test
  public void testGetWinningCountryCount ()
  {
    final int winPercentage = 87;
    final int totalCountryCount = 52;
    final int expectedWinningCountryCount = 46;
    final GameRules rules = new ClassicGameRules.Builder ().winPercentage (winPercentage)
            .totalCountryCount (totalCountryCount).build ();
    final int actualWinningCountryCount = rules.getWinningCountryCount ();

    assertEquals (expectedWinningCountryCount, actualWinningCountryCount);
  }

  @Test
  public void testIsValidWinPercentage ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
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
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    rules.isValidWinPercentage (Integer.MIN_VALUE);
  }

  @Test
  public void testGetInitialPlayerCountryDistributionUniformMaxPlayers ()
  {
    final Integer expectedUniformDistributionValue = 3;
    final ImmutableSet <Country> testCountrySet = PlayMapModelTest
            .generateTestCountries (ClassicGameRules.MAX_PLAYERS * expectedUniformDistributionValue);
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (testCountrySet.size ()).build ();

    final ImmutableList <Integer> testDistribution = rules
            .getInitialPlayerCountryDistribution (ClassicGameRules.MAX_PLAYERS);
    for (final Integer countryCount : testDistribution)
    {
      assertEquals (countryCount, expectedUniformDistributionValue);
    }
  }

  @Test
  public void testGetInitialPlayerCountryDistributionNonUniformMaxPlayers ()
  {
    final int testPlayerCount = ClassicGameRules.MAX_PLAYERS;
    final int expectedBaseDistributionValue = 3, expectedRemainderValue = testPlayerCount - 3;
    final ImmutableSet <Country> testCountrySet = PlayMapModelTest
            .generateTestCountries (testPlayerCount * expectedBaseDistributionValue + expectedRemainderValue);
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (testPlayerCount)
            .totalCountryCount (testCountrySet.size ()).build ();

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
    final int testPlayerCount = ClassicGameRules.MIN_PLAYERS;
    final int expectedBaseDistributionValue = 10, expectedRemainderValue = 1;
    final ImmutableSet <Country> testCountrySet = PlayMapModelTest
            .generateTestCountries (testPlayerCount * expectedBaseDistributionValue + expectedRemainderValue);
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (testPlayerCount)
            .totalCountryCount (testCountrySet.size ()).build ();

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
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.TYPE1, CardType.TYPE1, CardType.TYPE1)));
  }

  @Test
  public void testIsValidCardSetAllUnique ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.TYPE3, CardType.TYPE1, CardType.TYPE2)));
  }

  @Test
  public void testIsValidCardSetUniqueWithWildcard ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    assertTrue (rules.isValidCardSet (ImmutableList.of (CardType.WILDCARD, CardType.TYPE2, CardType.TYPE3)));
  }

  @Test
  public void testNotValidCardSetTwoWildcards ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    assertFalse (rules.isValidCardSet (ImmutableList.of (CardType.WILDCARD, CardType.WILDCARD, CardType.TYPE3)));
  }

  @Test
  public void testNotValidCardSetAllWildcards ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    assertFalse (rules.isValidCardSet (ImmutableList.of (CardType.WILDCARD, CardType.WILDCARD, CardType.WILDCARD)));
  }

  @Test
  public void testNotValidCardSetMismatchOrder1 ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    assertFalse (rules.isValidCardSet (ImmutableList.of (CardType.TYPE1, CardType.TYPE1, CardType.TYPE2)));
  }

  @Test
  public void testNotValidCardSetMismatchOrder2 ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    assertFalse (rules.isValidCardSet (ImmutableList.of (CardType.TYPE2, CardType.TYPE1, CardType.TYPE2)));
  }

  @Test
  public void testCalculateTradeInBonusReinforcementsZeroToFive ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    int expected = 4; // 4 at first trade in
    for (int i = 0; i < 5; i++, expected += 2)
    {
      assertEquals (expected, rules.calculateTradeInBonusReinforcements (i));
    }
  }

  @Test
  public void testCalculateTradeInBonusReinforcementsAfterFive ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();
    int expected = 15; // 15 after fifth trade in
    for (int i = 5; i < 100; i++, expected += 5)
    {
      assertEquals (expected, rules.calculateTradeInBonusReinforcements (i));
    }
  }
}
