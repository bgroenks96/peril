package com.forerunnergames.peril.core.model.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    assertTrue (rules.getMaxArmiesInHand () == ClassicGameRules.MAX_ARMIES_IN_HAND);
  }

  @Test
  public void testGetMaxPlayers ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertTrue (rules.getMaxPlayers () == ClassicGameRules.MAX_PLAYERS);
  }

  @Test
  public void testGetMinArmiesInHand ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertTrue (rules.getMinArmiesInHand () == ClassicGameRules.MIN_ARMIES_IN_HAND);
  }

  @Test
  public void testGetMinPlayers ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertTrue (rules.getMinPlayers () == ClassicGameRules.MIN_PLAYERS);
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

    assertTrue (rules.getMinPlayerLimit () == ClassicGameRules.MIN_PLAYER_LIMIT);
  }

  @Test
  public void testGetMaxTotalCountryCount ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertTrue (rules.getMaxTotalCountryCount () == ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT);
  }

  @Test
  public void testGetMinTotalCountryCount ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertTrue (rules.getMinTotalCountryCount () == ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT);
  }

  @Test
  public void testGetMaxPlayerLimit ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertTrue (rules.getMaxPlayerLimit () == ClassicGameRules.MAX_PLAYER_LIMIT);
  }

  @Test
  public void testGetMaxWinPercentage ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().build ();

    assertTrue (rules.getMaxWinPercentage () == ClassicGameRules.MAX_WIN_PERCENTAGE);
  }

  @Test
  public void testGetPlayerLimitForMaxPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MAX_PLAYER_LIMIT;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit).build ();

    assertTrue (rules.getPlayerLimit () == playerLimit);
  }

  @Test
  public void testGetPlayerLimitForMinPlayerLimit ()
  {
    final int playerLimit = ClassicGameRules.MIN_PLAYER_LIMIT;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (playerLimit).build ();

    assertTrue (rules.getPlayerLimit () == playerLimit);
  }

  @Test
  public void testGetTotalCountryCountForMaxCountryCount ()
  {
    final int totalCountryCount = ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT;
    final GameRules rules = new ClassicGameRules.Builder ().totalCountryCount (totalCountryCount).build ();

    assertTrue (rules.getTotalCountryCount () == totalCountryCount);
  }

  @Test
  public void testGetTotalCountryCountForMinCountryCount ()
  {
    final int totalCountryCount = ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT;
    final GameRules rules = new ClassicGameRules.Builder ().totalCountryCount (totalCountryCount).build ();

    assertTrue (rules.getTotalCountryCount () == totalCountryCount);
  }

  @Test
  public void testGetWinPercentageForMaxWinPercentage ()
  {
    final int winPercentage = ClassicGameRules.MAX_WIN_PERCENTAGE;
    final GameRules rules = new ClassicGameRules.Builder ().winPercentage (winPercentage).build ();

    assertTrue (rules.getWinPercentage () == winPercentage);
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
    assertFalse (rules.isValidWinPercentage (Integer.MIN_VALUE));
    assertFalse (rules.isValidWinPercentage (0));
    assertFalse (rules.isValidWinPercentage (maxWinPercentage + 1));
  }
}
