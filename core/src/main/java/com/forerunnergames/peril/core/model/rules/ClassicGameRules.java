package com.forerunnergames.peril.core.model.rules;

import com.forerunnergames.peril.core.model.TurnPhase;
import com.forerunnergames.peril.core.model.card.CardType;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableList;

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
  private static final int CARD_TRADE_IN_COUNT = 3;
  private static final int MAX_CARDS_IN_HAND_REINFORCE_PHASE = 6;
  private static final int MAX_CARDS_IN_HAND_ATTACK_PHASE = 9;
  private static final int MAX_CARDS_IN_HAND_FORTIFY_PHASE = MAX_CARDS_IN_HAND_REINFORCE_PHASE;
  private static final int MIN_CARDS_IN_HAND_FOR_TRADE_IN_REINFORCE_PHASE = CARD_TRADE_IN_COUNT;
  private static final int MIN_CARDS_IN_HAND_FOR_TRADE_IN_ATTACK_PHASE = 6;
  private static final int MIN_CARDS_IN_HAND_TO_REQUIRE_TRADE_IN_REINFORCE_PHASE = 5;
  private static final int MIN_CARDS_IN_HAND_TO_REQUIRE_TRADE_IN_ATTACK_PHASE = MIN_CARDS_IN_HAND_FOR_TRADE_IN_ATTACK_PHASE;
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
    return MIN_PLAYER_LIMIT;
  }

  @Override
  public int getMaxPlayerLimit ()
  {
    return MAX_PLAYER_LIMIT;
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
    return MAX_WIN_PERCENTAGE;
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
  public int getCardTradeInCount ()
  {
    return CARD_TRADE_IN_COUNT;
  }

  @Override
  public int getMaxCardsInHand (final TurnPhase turnPhase)
  {
    Arguments.checkIsNotNull (turnPhase, "turnPhase");

    switch (turnPhase)
    {
      case REINFORCE:
      {
        return MAX_CARDS_IN_HAND_REINFORCE_PHASE;
      }
      case ATTACK:
      {
        return MAX_CARDS_IN_HAND_ATTACK_PHASE;
      }
      case FORTIFY:
      {
        return MAX_CARDS_IN_HAND_FORTIFY_PHASE;
      }
      default:
      {
        throw new IllegalArgumentException ("Illegal value for [" + TurnPhase.class.getSimpleName () + "].");
      }
    }
  }

  @Override
  public int getMinCardsInHandForTradeInReinforcePhase ()
  {
    return MIN_CARDS_IN_HAND_FOR_TRADE_IN_REINFORCE_PHASE;
  }

  @Override
  public int getMinCardsInHandToRequireTradeIn (final TurnPhase turnPhase)
  {
    Arguments.checkIsNotNull (turnPhase, "turnPhase");

    switch (turnPhase)
    {
      case REINFORCE:
      {
        return MIN_CARDS_IN_HAND_TO_REQUIRE_TRADE_IN_REINFORCE_PHASE;
      }
      case ATTACK:
      {
        return MIN_CARDS_IN_HAND_TO_REQUIRE_TRADE_IN_ATTACK_PHASE;
      }
      default:
      {
        throw new IllegalArgumentException ("Cannot trade in during: [" + turnPhase + "].");
      }
    }
  }

  @Override
  public boolean isValidCardSet (final ImmutableList <CardType> cardTypes)
  {
    Arguments.checkIsNotNull (cardTypes, "cardTypes");
    Arguments.checkHasNoNullElements (cardTypes, "cardTypes");

    final int matchLen = cardTypes.size ();
    if (matchLen != getCardTradeInCount ()) return false;

    // build the match string (string of type values) from cardTypes
    final StringBuilder matchStrBuilder = new StringBuilder ();
    for (final CardType type : cardTypes)
    {
      matchStrBuilder.append (type.getTypeValue ());
    }
    final String matchStr = matchStrBuilder.toString ();
    // pattern 1: "(\\d)\\1{2}" matches repeating integers, excluding zero; i.e. 3 matching non-wild cards
    // pattern 2: ^(?:(\\d)(?!.*\\2)){3,} matches strings of integers with no repeats; i.e. 3 unique types
    // wildcard logic is implied by pattern 2; for example, wildcard (0) + 2 unique types is a unique int string
    final String matchExp = String.format ("([1-9])\\1{%d}|^(?:(\\d)(?!.*\\2)){%d,}", matchLen - 1, matchLen);
    return matchStr.matches (matchExp);
  }

  @Override
  public boolean isValidWinPercentage (final int winPercentage)
  {
    Arguments.checkIsNotNegative (winPercentage, "winPercentage");

    return winPercentage >= minWinPercentage && winPercentage <= MAX_WIN_PERCENTAGE;
  }

  /**
   * @return an ImmutableList of length 'playerCount' containing ordered country-per-player distribution values.
   */
  @Override
  public ImmutableList <Integer> getInitialPlayerCountryDistribution (final int playerCount)
  {
    Arguments.checkIsNotNegative (playerCount, "playerCount");
    Arguments.checkLowerInclusiveBound (playerCount, MIN_PLAYERS, "playerCount");
    Arguments.checkUpperInclusiveBound (playerCount, playerLimit, "playerCount");

    // return immediately for zero players to avoid a zero divisor error
    // this is only included in case for any reason MIN_PLAYERS becomes 0
    if (playerCount == 0) return ImmutableList.of ();

    final ImmutableList.Builder <Integer> listBuilder = ImmutableList.builder ();
    final int countryCount = getTotalCountryCount ();
    final int baseCountriesPerPlayer = countryCount / playerCount;
    int countryPerPlayerRemainder = countryCount % playerCount;
    for (int i = 0; i < playerCount; i++)
    {
      int playerCountryCount = baseCountriesPerPlayer;
      // as long as there is a remainder left, add one to the country count
      if (countryPerPlayerRemainder > 0)
      {
        ++playerCountryCount;
      }
      listBuilder.add (playerCountryCount);
      // decrement country remainder
      --countryPerPlayerRemainder;
    }
    return listBuilder.build ();
  }

  /**
   * Calculates number of reinforcements for a player with the given number of owned countries. Note that this method
   * does not take into account any granted bonuses (i.e. continents, cards, etc).
   */
  @Override
  public int calculateCountryReinforcements (final int ownedCountryCount)
  {
    Arguments.checkIsNotNegative (ownedCountryCount, "ownedCountryCount");
    Arguments.checkLowerExclusiveBound (ownedCountryCount, 0, "ownedCountryCount");

    return (int) Math.floor (ownedCountryCount / 3.0f); // floor function included for clarity
  }

  // @formatter:off
  /**
   * Calculates number of bonus reinforcements a player should get for a valid card trade-in with the given
   * 'globalTradeInCount.' This is defined in ClassicGameRules by the following piecewise function:
   *
   * F(n) = | 4 + 2n          if 0 <= n < 5
   *        | 15 + 5(n - 5)   if n >= 5
   *
   * @param globalTradeInCount
   *          number of card sets traded in by players so far in the game
   */
  // @formatter:on
  @Override
  public int calculateTradeInBonusReinforcements (final int globalTradeInCount)
  {
    Arguments.checkIsNotNegative (globalTradeInCount, "globalTradeInCount");

    if (globalTradeInCount < 5)
    {
      return 4 + 2 * globalTradeInCount;
    }
    else
    {
      return 15 + 5 * (globalTradeInCount - 5);
    }
  }

  // @formatter:off
  /**
   * Defined in ClassicGameRules by the following piecewise function:
   *
   * F(n) = | 5               if n = 10
   *        | 40 - 5*(n - 2)  if n < 10
   *
   * where 'F' is the number of armies returned in the set and 'n' is the number of players in the given PlayerModel.
   */
  // @formatter:on
  private static int calculateInitialArmies (final int playerLimit)
  {
    return playerLimit < 10 ? 40 - 5 * (playerLimit - 2) : 5;
  }

  private static int calculateMinWinPercentage (final int playerLimit, final int totalCountryCount)
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

  private static int calculateWinningCountryCount (final int winPercentage, final int totalCountryCount)
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

    minWinPercentage = calculateMinWinPercentage (playerLimit, totalCountryCount);

    Arguments.checkLowerInclusiveBound (winPercentage, minWinPercentage, "winPercentage");

    this.playerLimit = playerLimit;
    this.winPercentage = winPercentage;
    this.totalCountryCount = totalCountryCount;
    this.initialCountryAssignment = initialCountryAssignment;
    initialArmies = calculateInitialArmies (playerLimit);
    winningCountryCount = calculateWinningCountryCount (winPercentage, totalCountryCount);
    // @formatter:on
  }
}
