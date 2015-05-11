package com.forerunnergames.peril.core.model.rules;

import com.forerunnergames.tools.common.Arguments;

import javax.annotation.Nullable;

public final class ClassicGameRules implements GameRules
{
  public static final int MIN_PLAYERS = 2;
  public static final int MAX_PLAYERS = 10;
  public static final int MIN_PLAYER_LIMIT = MIN_PLAYERS;
  public static final int MAX_PLAYER_LIMIT = MAX_PLAYERS;
  public static final int MAX_WIN_PERCENTAGE = 100;
  public static final int MIN_TOTAL_COUNTRY_COUNT = MAX_PLAYERS;
  public static final int MAX_TOTAL_COUNTRY_COUNT = 1000;
  public static final int MIN_ARMIES_IN_HAND = 0;
  public static final int MAX_ARMIES_IN_HAND = Integer.MAX_VALUE;
  public static final int DEFAULT_PLAYER_LIMIT = MIN_PLAYER_LIMIT;
  public static final int DEFAULT_WIN_PERCENTAGE = MAX_WIN_PERCENTAGE;
  public static final int DEFAULT_TOTAL_COUNTRY_COUNT = MIN_TOTAL_COUNTRY_COUNT;
  public static final InitialCountryAssignment DEFAULT_INITIAL_COUNTRY_ASSIGNMENT = InitialCountryAssignment.RANDOM;
  private final int playerLimit;
  private final int winPercentage;
  private final int minWinPercentage;
  private final int totalCountryCount;
  private final int initialArmies;
  private final int winningCountryCount;
  private final InitialCountryAssignment initialCountryAssignment;

  @Override
  public int getInitialArmies ()
  {
    return initialArmies;
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return initialCountryAssignment;
  }

  @Override
  public int getMinArmiesInHand ()
  {
    return MIN_ARMIES_IN_HAND;
  }

  @Override
  public int getMaxArmiesInHand ()
  {
    return MAX_ARMIES_IN_HAND;
  }

  @Override
  public int getMinPlayerLimit ()
  {
    return ClassicGameRules.MIN_PLAYER_LIMIT;
  }

  @Override
  public int getMaxPlayerLimit ()
  {
    return ClassicGameRules.MAX_PLAYER_LIMIT;
  }

  @Override
  public int getMinPlayers ()
  {
    return MIN_PLAYERS;
  }

  @Override
  public int getMaxPlayers ()
  {
    return MAX_PLAYERS;
  }

  @Override
  public int getMinTotalCountryCount ()
  {
    return MIN_TOTAL_COUNTRY_COUNT;
  }

  @Override
  public int getMaxTotalCountryCount ()
  {
    return MAX_TOTAL_COUNTRY_COUNT;
  }

  @Override
  public int getMinWinPercentage ()
  {
    return minWinPercentage;
  }

  @Override
  public int getMaxWinPercentage ()
  {
    return ClassicGameRules.MAX_WIN_PERCENTAGE;
  }

  @Override
  public int getPlayerLimit ()
  {
    return playerLimit;
  }

  @Override
  public int getTotalCountryCount ()
  {
    return totalCountryCount;
  }

  @Override
  public int getWinPercentage ()
  {
    return winPercentage;
  }

  @Override
  public int getWinningCountryCount ()
  {
    return winningCountryCount;
  }

  @Override
  public boolean isValidWinPercentage (final int winPercentage)
  {
    return winPercentage >= minWinPercentage && winPercentage <= MAX_WIN_PERCENTAGE;
  }

  // @formatter:off
  /**
   * Defined in ClassicGameRules by the following piecewise function:
   *
   * P(n) = | 1               if n = 10
   *        | 40 - 5*(n - 2)  if n < 10
   *
   * where 'P' is the number of armies returned in the set and 'n' is the number of players in the given PlayerModel.
   */
  // @formatter:on
  private int calculateInitialArmies (final int playerLimit)
  {
    return playerLimit < 10 ? 40 - 5 * (playerLimit - 2) : 5;
  }

  private int calculateMinWinPercentage (final int playerLimit, final int totalCountryCount)
  {
    // @formatter:off
    // If country distribution does not divide evenly, some players will receive at most one extra country.
    // This will correctly calculate the maximum number of countries any player will be initially distributed, even in the case of uneven distribution.
    final int maxCountriesAnyPlayerWillBeDistributed = (int) Math.ceil (totalCountryCount / (double) playerLimit);
    final int maxOwnershipPercentageAnyPlayerWillBeDistributed = (int) Math.ceil (maxCountriesAnyPlayerWillBeDistributed / (double) totalCountryCount * 100.0);
    // @formatter:on

    // Ensure that the win percentage will not be met upon initial distribution by any player having the max initial
    // ownership percentage. Adding 1 to the max distribution ownership percentage will require a player owning the
    // most countries after initial distribution to have to conquer at least one additional country to meet the minimum
    // win percentage.
    return maxOwnershipPercentageAnyPlayerWillBeDistributed + 1;
  }

  private int calculateWinningCountryCount (final int winPercentage, final int totalCountryCount)
  {
    // The ceiling function ensures that if the win percentage includes a fraction of a country, that it will always
    // round up to the nearest country count.
    return (int) Math.ceil (winPercentage / 100.0 * totalCountryCount);
  }

  public static final class Builder
  {
    private int playerLimit = DEFAULT_PLAYER_LIMIT;
    private int winPercentage = DEFAULT_WIN_PERCENTAGE;
    private int totalCountryCount = DEFAULT_TOTAL_COUNTRY_COUNT;
    private InitialCountryAssignment initialCountryAssignment = DEFAULT_INITIAL_COUNTRY_ASSIGNMENT;

    public ClassicGameRules build ()
    {
      return new ClassicGameRules (playerLimit, winPercentage, totalCountryCount, initialCountryAssignment);
    }

    public Builder initialCountryAssignment (@Nullable final InitialCountryAssignment initialCountryAssignment)
    {
      if (initialCountryAssignment == null) return this;

      this.initialCountryAssignment = initialCountryAssignment;

      return this;
    }

    public Builder playerLimit (@Nullable final Integer playerLimit)
    {
      if (playerLimit == null) return this;

      this.playerLimit = playerLimit;

      return this;
    }

    public Builder totalCountryCount (@Nullable final Integer totalCountryCount)
    {
      if (totalCountryCount == null) return this;

      this.totalCountryCount = totalCountryCount;

      return this;
    }

    public Builder winPercentage (@Nullable final Integer winPercentage)
    {
      if (winPercentage == null) return this;

      this.winPercentage = winPercentage;

      return this;
    }
  }

  private ClassicGameRules (final int playerLimit,
                            final int winPercentage,
                            final int totalCountryCount,
                            final InitialCountryAssignment initialCountryAssignment)
  {
    // @formatter:off
    Arguments.checkLowerInclusiveBound (playerLimit, MIN_PLAYER_LIMIT, "playerLimit", "ClassicGameRules.MIN_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (playerLimit, MAX_PLAYER_LIMIT, "playerLimit", "ClassicGameRules.MAX_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (winPercentage, MAX_WIN_PERCENTAGE, "winPercentage", "ClassicGameRules.MAX_WIN_PERCENTAGE");
    Arguments.checkLowerInclusiveBound (totalCountryCount, MIN_TOTAL_COUNTRY_COUNT, "totalCountryCount", "ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT");
    Arguments.checkUpperInclusiveBound (totalCountryCount, MAX_TOTAL_COUNTRY_COUNT, "totalCountryCount", "ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT");
    Arguments.checkIsNotNull (initialCountryAssignment, "initialCountryAssignment");

    this.minWinPercentage = calculateMinWinPercentage (playerLimit, totalCountryCount);

    Arguments.checkLowerInclusiveBound (winPercentage, minWinPercentage, "winPercentage");

    this.playerLimit = playerLimit;
    this.winPercentage = winPercentage;
    this.totalCountryCount = totalCountryCount;
    this.initialCountryAssignment = initialCountryAssignment;
    this.initialArmies = calculateInitialArmies (playerLimit);
    this.winningCountryCount = calculateWinningCountryCount (winPercentage, totalCountryCount);
    // @formatter:on
  }
}
