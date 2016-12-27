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

import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;

public final class ClassicGameRules implements GameRules
{
  // @formatter:off
  public static final int MIN_TOTAL_PLAYERS = 2;
  public static final int MAX_TOTAL_PLAYERS = 10;
  public static final int MIN_HUMAN_PLAYERS = 0;
  public static final int MAX_HUMAN_PLAYERS = 10;
  public static final int MIN_AI_PLAYERS = 0;
  public static final int MAX_AI_PLAYERS = 10;
  public static final int MIN_SPECTATORS = 0;
  public static final int MAX_SPECTATORS = 6;
  public static final int MIN_TOTAL_PLAYER_LIMIT = MIN_TOTAL_PLAYERS;
  public static final int MAX_TOTAL_PLAYER_LIMIT = MAX_TOTAL_PLAYERS;
  public static final int MIN_HUMAN_PLAYER_LIMIT = MIN_HUMAN_PLAYERS;
  public static final int MAX_HUMAN_PLAYER_LIMIT = MAX_HUMAN_PLAYERS;
  public static final int MIN_AI_PLAYER_LIMIT = MIN_AI_PLAYERS;
  public static final int MAX_AI_PLAYER_LIMIT = MAX_AI_PLAYERS;
  public static final int MIN_SPECTATOR_LIMIT = MIN_SPECTATORS;
  public static final int MAX_SPECTATOR_LIMIT = MAX_SPECTATORS;
  public static final int DEFAULT_TOTAL_PLAYER_LIMIT = MIN_TOTAL_PLAYER_LIMIT;
  public static final int DEFAULT_AI_PLAYER_LIMIT = MIN_AI_PLAYER_LIMIT;
  public static final int DEFAULT_HUMAN_PLAYER_LIMIT = MIN_TOTAL_PLAYER_LIMIT;
  public static final int DEFAULT_SPECTATOR_LIMIT = MIN_SPECTATOR_LIMIT;
  public static final int MAX_WIN_PERCENTAGE = 100;
  public static final int MIN_TOTAL_COUNTRY_COUNT = MAX_TOTAL_PLAYERS;
  public static final int MAX_TOTAL_COUNTRY_COUNT = 1000;
  public static final int MIN_PLAYER_COUNTRY_COUNT = 1;
  public static final int MIN_ARMIES_IN_HAND = 0;
  public static final int MAX_ARMIES_IN_HAND = Integer.MAX_VALUE;
  public static final int MIN_ARMIES_ON_COUNTRY = 0;
  public static final int MAX_ARMIES_ON_COUNTRY = 99;
  public static final int MIN_ARMIES_ON_COUNTRY_FOR_ATTACK = 2;
  public static final int MIN_ARMIES_ON_COUNTRY_FOR_DEFEND = 1;
  public static final int ABSOLUTE_MIN_CARDS_IN_HAND = 0;
  public static final int ABSOLUTE_MAX_CARDS_IN_HAND = 9;
  public static final int MIN_ARMIES_ON_SOURCE_COUNTRY_FOR_FORTIFY = 2;
  public static final int MAX_ARMIES_ON_TARGET_COUNTRY_FOR_FORTIFY = MAX_ARMIES_ON_COUNTRY - 1;
  public static final int DEFAULT_WIN_PERCENTAGE = MAX_WIN_PERCENTAGE;
  public static final int DEFAULT_TOTAL_COUNTRY_COUNT = MIN_TOTAL_COUNTRY_COUNT;
  public static final InitialCountryAssignment DEFAULT_INITIAL_COUNTRY_ASSIGNMENT = InitialCountryAssignment.RANDOM;
  public static final int ABSOLUTE_MIN_ATTACKER_DIE_COUNT = 1;
  public static final int ABSOLUTE_MAX_ATTACKER_DIE_COUNT = 3;
  public static final int ABSOLUTE_MIN_DEFENDER_DIE_COUNT = 1;
  public static final int ABSOLUTE_MAX_DEFENDER_DIE_COUNT = 2;
  public static final DieRange ABSOLUTE_ATTACKER_DIE_RANGE = new DieRange (ABSOLUTE_MIN_ATTACKER_DIE_COUNT, ABSOLUTE_MAX_ATTACKER_DIE_COUNT);
  public static final DieRange ABSOLUTE_DEFENDER_DIE_RANGE = new DieRange (ABSOLUTE_MIN_DEFENDER_DIE_COUNT, ABSOLUTE_MAX_DEFENDER_DIE_COUNT);
  private static final int MIN_REINFORCEMENTS_PLACED_PER_COUNTRY = 1;
  private static final int MIN_COUNTRY_REINFORCEMENTS_RECEIVED = 3;
  private static final int CARD_TRADE_IN_COUNT = 3;
  private static final int MAX_CARDS_IN_HAND_REINFORCE_PHASE = 6;
  private static final int MAX_CARDS_IN_HAND_ATTACK_PHASE = 9;
  private static final int MAX_CARDS_IN_HAND_FORTIFY_PHASE = MAX_CARDS_IN_HAND_REINFORCE_PHASE;
  private static final int MIN_CARDS_IN_HAND_FOR_TRADE_IN_REINFORCE_PHASE = CARD_TRADE_IN_COUNT;
  private static final int MIN_CARDS_IN_HAND_FOR_TRADE_IN_ATTACK_PHASE = 6;
  private static final int MIN_CARDS_IN_HAND_TO_REQUIRE_TRADE_IN_REINFORCE_PHASE = 5;
  private static final int MIN_CARDS_IN_HAND_TO_REQUIRE_TRADE_IN_ATTACK_PHASE = MIN_CARDS_IN_HAND_FOR_TRADE_IN_ATTACK_PHASE;
  private final PersonLimits personLimits;
  private final int winPercentage;
  private final int minWinPercentage;
  private final int totalCountryCount;
  private final int initialArmies;
  private final int winningCountryCount;
  private final InitialCountryAssignment initialCountryAssignment;
  // @formatter:on

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
  public int getMinPlayerCountryCount ()
  {
    return MIN_PLAYER_COUNTRY_COUNT;
  }

  @Override
  public int getMaxPlayerCountryCount ()
  {
    return getWinningCountryCount () - 1;
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
  public int getMinArmiesOnCountry ()
  {
    return MIN_ARMIES_ON_COUNTRY;
  }

  @Override
  public int getMaxArmiesOnCountry ()
  {
    return MAX_ARMIES_ON_COUNTRY;
  }

  @Override
  public int getMinArmiesOnCountryForAttack ()
  {
    return MIN_ARMIES_ON_COUNTRY_FOR_ATTACK;
  }

  @Override
  public int getMinArmiesOnCountryForDefend ()
  {
    return MIN_ARMIES_ON_COUNTRY_FOR_DEFEND;
  }

  @Override
  public int getMinArmiesOnSourceCountryForFortify ()
  {
    return MIN_ARMIES_ON_SOURCE_COUNTRY_FOR_FORTIFY;
  }

  @Override
  public int getMaxArmiesOnTargetCountryForFortify ()
  {
    return MAX_ARMIES_ON_TARGET_COUNTRY_FOR_FORTIFY;
  }

  @Override
  public int getMinTotalPlayers ()
  {
    return MIN_TOTAL_PLAYERS;
  }

  @Override
  public int getMaxTotalPlayers ()
  {
    return MAX_TOTAL_PLAYERS;
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
  public int getMinTotalPlayerLimit ()
  {
    return MIN_TOTAL_PLAYER_LIMIT;
  }

  @Override
  public int getMaxTotalPlayerLimit ()
  {
    return MAX_TOTAL_PLAYER_LIMIT;
  }

  @Override
  public int getTotalPlayerLimit ()
  {
    return personLimits.getTotalPlayerLimit ();
  }

  @Override
  public int getPlayerLimitFor (final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (sentience, "sentience");

    return personLimits.getPlayerLimitFor (sentience);
  }

  @Override
  public int getSpectatorLimit ()
  {
    return personLimits.getSpectatorLimit ();
  }

  @Override
  public PersonLimits getPersonLimits ()
  {
    return personLimits;
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
  public int getMinReinforcementsPlacedPerCountry ()
  {
    return MIN_REINFORCEMENTS_PLACED_PER_COUNTRY;
  }

  @Override
  public int getCardTradeInCount ()
  {
    return CARD_TRADE_IN_COUNT;
  }

  @Override
  public int getMaxCardsInHand (final TurnPhase phase)
  {
    Arguments.checkIsNotNull (phase, "phase");

    switch (phase)
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
  public int getAbsoluteMaxCardsInHand ()
  {
    return ABSOLUTE_MAX_CARDS_IN_HAND;
  }

  @Override
  public int getAbsoluteMinCardsInHand ()
  {
    return ABSOLUTE_MIN_CARDS_IN_HAND;
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
  public int getAbsoluteMinAttackerDieCount ()
  {
    return ABSOLUTE_MIN_ATTACKER_DIE_COUNT;
  }

  @Override
  public int getAbsoluteMaxAttackerDieCount ()
  {
    return ABSOLUTE_MAX_ATTACKER_DIE_COUNT;
  }

  @Override
  public int getAbsoluteMinDefenderDieCount ()
  {
    return ABSOLUTE_MIN_DEFENDER_DIE_COUNT;
  }

  @Override
  public int getAbsoluteMaxDefenderDieCount ()
  {
    return ABSOLUTE_MAX_DEFENDER_DIE_COUNT;
  }

  @Override
  public DieRange getAbsoluteAttackerDieRange ()
  {
    return ABSOLUTE_ATTACKER_DIE_RANGE;
  }

  @Override
  public DieRange getAbsoluteDefenderDieRange ()
  {
    return ABSOLUTE_DEFENDER_DIE_RANGE;
  }

  @Override
  public int getMinAttackerDieCount (final int attackingCountryArmyCount)
  {
    Arguments.checkIsNotNegative (attackingCountryArmyCount, "attackingCountryArmyCount");

    return attackingCountryArmyCount > 1 ? ABSOLUTE_MIN_ATTACKER_DIE_COUNT : 0;
  }

  @Override
  public int getMaxAttackerDieCount (final int attackingCountryArmyCount)
  {
    Arguments.checkIsNotNegative (attackingCountryArmyCount, "attackingCountryArmyCount");

    if (attackingCountryArmyCount > 3) return ABSOLUTE_MAX_ATTACKER_DIE_COUNT;

    return attackingCountryArmyCount == 3 || attackingCountryArmyCount == 2 ? attackingCountryArmyCount - 1 : 0;
  }

  @Override
  public DieRange getAttackerDieRange (final int attackingCountryArmyCount)
  {
    Arguments.checkIsNotNegative (attackingCountryArmyCount, "attackingCountryArmyCount");

    return new DieRange (getMinAttackerDieCount (attackingCountryArmyCount),
            getMaxAttackerDieCount (attackingCountryArmyCount));
  }

  @Override
  public int getMinDefenderDieCount (final int defendingCountryArmyCount)
  {
    Arguments.checkIsNotNegative (defendingCountryArmyCount, "defendingCountryArmyCount");

    return defendingCountryArmyCount > 0 ? ABSOLUTE_MIN_DEFENDER_DIE_COUNT : 0;
  }

  @Override
  public int getMaxDefenderDieCount (final int defendingCountryArmyCount)
  {
    Arguments.checkIsNotNegative (defendingCountryArmyCount, "defendingCountryArmyCount");

    if (defendingCountryArmyCount > 1) return ABSOLUTE_MAX_DEFENDER_DIE_COUNT;

    return defendingCountryArmyCount == 1 ? 1 : 0;
  }

  @Override
  public DieRange getDefenderDieRange (final int defendingCountryArmyCount)
  {
    Arguments.checkIsNotNegative (defendingCountryArmyCount, "defendingCountryArmyCount");

    return new DieRange (getMinDefenderDieCount (defendingCountryArmyCount),
            getMaxDefenderDieCount (defendingCountryArmyCount));
  }

  @Override
  public int getMinOccupyArmyCount (final int attackingPlayerDieCount)
  {
    Arguments.checkIsNotNegative (attackingPlayerDieCount, "attackingPlayerDieCount");

    return attackingPlayerDieCount;
  }

  @Override
  public int getMaxOccupyArmyCount (final int attackingCountryArmyCount)
  {
    Arguments.checkIsNotNegative (attackingCountryArmyCount, "attackingCountryArmyCount");

    return attackingCountryArmyCount - 1;
  }

  @Override
  public int getMinFortifyDeltaArmyCount (final int sourceCountryArmyCount, final int targetCountryArmyCount)
  {
    Arguments.checkIsNotNegative (sourceCountryArmyCount, "sourceCountryArmyCount");
    Arguments.checkIsNotNegative (targetCountryArmyCount, "targetCountryArmyCount");

    if (sourceCountryArmyCount < MIN_ARMIES_ON_SOURCE_COUNTRY_FOR_FORTIFY) return 0;
    if (targetCountryArmyCount > MAX_ARMIES_ON_TARGET_COUNTRY_FOR_FORTIFY) return 0;

    return 1;
  }

  @Override
  public int getMaxFortifyDeltaArmyCount (final int sourceCountryArmyCount, final int targetCountryArmyCount)
  {
    Arguments.checkIsNotNegative (sourceCountryArmyCount, "sourceCountryArmyCount");
    Arguments.checkIsNotNegative (targetCountryArmyCount, "targetCountryArmyCount");

    if (sourceCountryArmyCount < MIN_ARMIES_ON_SOURCE_COUNTRY_FOR_FORTIFY) return 0;
    if (targetCountryArmyCount > MAX_ARMIES_ON_TARGET_COUNTRY_FOR_FORTIFY) return 0;

    return Math.min (sourceCountryArmyCount - 1, MAX_ARMIES_ON_COUNTRY - targetCountryArmyCount);
  }

  @Override
  public DieOutcome determineAttackerOutcome (final DieFaceValue attackerDie, final DieFaceValue defenderDie)
  {
    Arguments.checkIsNotNull (attackerDie, "attackerDie");
    Arguments.checkIsNotNull (defenderDie, "defenderDie");

    // This is the outcome from the perspective of the attacker.
    // An attacker's die value must be strictly greater than the defender's die value to win.
    // Thus, the attacker loses in the case of a tie.

    return attackerDie.value () > defenderDie.value () ? DieOutcome.WIN : DieOutcome.LOSE;
  }

  @Override
  public DieOutcome determineDefenderOutcome (final DieFaceValue defenderDie, final DieFaceValue attackerDie)
  {
    Arguments.checkIsNotNull (defenderDie, "defenderDie");
    Arguments.checkIsNotNull (attackerDie, "attackerDie");

    // This is the outcome from the perspective of the defender.
    // A defender's die value may be greater than or equal to the attacker's die value to win.
    // Thus, the defender wins in the case of a tie.

    return defenderDie.value () >= attackerDie.value () ? DieOutcome.WIN : DieOutcome.LOSE;
  }

  /**
   * @return an ImmutableList of length 'playerCount' containing ordered countries-per-player distribution values.
   */
  @Override
  public ImmutableList <Integer> getInitialPlayerCountryDistribution (final int playerCount)
  {
    Arguments.checkIsNotNegative (playerCount, "playerCount");
    Arguments.checkLowerInclusiveBound (playerCount, MIN_TOTAL_PLAYERS, "playerCount",
                                        "ClassicGameRules.MIN_TOTAL_PLAYERS");
    Arguments.checkUpperInclusiveBound (playerCount, getTotalPlayerLimit (), "playerCount",
                                        "ClassicGameRules.getTotalPlayerLimit()");

    // return immediately for zero players to avoid a zero divisor error
    // this is only included in case for any reason MIN_TOTAL_PLAYERS becomes 0
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
    Arguments.checkLowerExclusiveBound (ownedCountryCount, 0, "ownedCountryCount");

    final int reinforcementCount = (int) Math.floor (ownedCountryCount / 3.0f); // floor function included for clarity

    return Math.max (reinforcementCount, MIN_COUNTRY_REINFORCEMENTS_RECEIVED);
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

  @Override
  public boolean isValidWinPercentage (final int winPercentage)
  {
    Arguments.checkIsNotNegative (winPercentage, "winPercentage");

    return winPercentage >= minWinPercentage && winPercentage <= MAX_WIN_PERCENTAGE;
  }

  @Override
  public boolean isValidCardSet (final ImmutableList <CardType> cardTypes)
  {
    Arguments.checkIsNotNull (cardTypes, "cardTypes");
    Arguments.checkHasNoNullElements (cardTypes, "cardTypes");

    final int matchLen = cardTypes.size ();
    if (matchLen != getCardTradeInCount ()) return false;

    // the wildcard is equal to a bitwise OR of all card types
    final int validUniqueMask = CardType.WILDCARD.getTypeValue ();

    // OR together all values; note that wildcards will always cause the
    // resulting value to be valid
    int maskUnique = 0;
    for (int i = 0; i < matchLen; i++)
    {
      final int typeValue = cardTypes.get (i).getTypeValue ();
      maskUnique |= typeValue;
    }

    // if the values all OR up to be equal to the WILDCARD bitmask, it's valid
    if (maskUnique == validUniqueMask)
    {
      return true;
    }

    // check if all values in the collection are the same by checking that the
    // frequency of the first value is equal to the length of the set
    final CardType first = Iterables.getFirst (cardTypes, CardType.WILDCARD);
    return Iterables.frequency (cardTypes, first) == matchLen;
  }

  @Override
  public boolean canBattle (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    return attackerCanBattle (attackingCountryArmies) && defenderCanBattle (defendingCountryArmies);
  }

  @Override
  public boolean attackerCanBattle (final int attackingCountryArmies)
  {
    return attackingCountryArmies >= MIN_ARMIES_ON_COUNTRY_FOR_ATTACK;
  }

  @Override
  public boolean defenderCanBattle (final int defendingCountryArmies)
  {
    return defendingCountryArmies >= MIN_ARMIES_ON_COUNTRY_FOR_DEFEND;
  }

  @Override
  public BattleOutcome getBattleOutcome (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    final boolean attackerCanBattle = attackerCanBattle (attackingCountryArmies);
    final boolean defenderCanBattle = defenderCanBattle (defendingCountryArmies);

    if (attackerCanBattle && defenderCanBattle) return BattleOutcome.CONTINUE;
    if (attackerCanBattle) return BattleOutcome.ATTACKER_VICTORIOUS;

    return BattleOutcome.ATTACKER_DEFEATED;
  }

  public static int getMinHumanPlayerLimit (final int aiPlayerLimit)
  {
    Arguments.checkLowerInclusiveBound (aiPlayerLimit, MIN_AI_PLAYER_LIMIT, "aiPlayerLimit",
                                        "GameSettings.MIN_AI_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (aiPlayerLimit, MAX_TOTAL_PLAYER_LIMIT, "aiPlayerLimit",
                                        "GameSettings.MAX_AI_PLAYER_LIMIT");

    // @formatter:off
    //
    // Guarantees there will be at least enough total players (AI + human) to meet MIN_TOTAL_PLAYER_LIMIT requirement.
    //
    // if aiPlayerLimit >= MIN_TOTAL_PLAYER_LIMIT (2), then minHumanPlayerLimit = MIN_HUMAN_PLAYER_LIMIT (0)
    // if aiPlayerLimit < MIN_TOTAL_PLAYER_LIMIT (2), then minHumanPlayerLimit = MIN_TOTAL_PLAYER_LIMIT (2) - aiPlayerLimit (0 or 1)
    //
    // More specifically:
    //
    // aiPlayerLimit  = 0, minHumanPlayerLimit = 2 (total min players:    2)
    // aiPlayerLimit  = 1, minHumanPlayerLimit = 1 (total min players:    2)
    // aiPlayerLimit >= 2, minHumanPlayerLimit = 0 (total min players: >= 2)
    //
    // @formatter:on
    final int minHumanPlayerLimit = Math.max (MIN_TOTAL_PLAYER_LIMIT - aiPlayerLimit, MIN_HUMAN_PLAYER_LIMIT);

    // Sanity checks.
    assert aiPlayerLimit + minHumanPlayerLimit >= MIN_TOTAL_PLAYER_LIMIT;
    assert aiPlayerLimit + minHumanPlayerLimit <= MAX_TOTAL_PLAYER_LIMIT;

    return minHumanPlayerLimit;
  }

  public static int getMaxHumanPlayerLimit (final int aiPlayerLimit)
  {
    Arguments.checkLowerInclusiveBound (aiPlayerLimit, MIN_AI_PLAYER_LIMIT, "aiPlayerLimit",
                                        "GameSettings.MIN_AI_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (aiPlayerLimit, MAX_TOTAL_PLAYER_LIMIT, "aiPlayerLimit",
                                        "GameSettings.MAX_AI_PLAYER_LIMIT");

    // @formatter:off
    //
    // Guarantees there will be at most total players (human + AI) to meet MAX_TOTAL_PLAYER_LIMIT requirement.
    //
    // More specifically:
    //
    // aiPlayerLimit = 10, maxHumanPlayerLimit =  0 (total max players: 10)
    // aiPlayerLimit =  9, maxHumanPlayerLimit =  1 (total max players: 10)
    // aiPlayerLimit =  8, maxHumanPlayerLimit =  2 (total max players: 10)
    // ...
    // aiPlayerLimit =  0, maxHumanPlayerLimit = 10 (total max players: 10)
    //
    // @formatter:on
    final int maxHumanPlayerLimit = MAX_TOTAL_PLAYER_LIMIT - aiPlayerLimit;

    // Sanity checks.
    assert aiPlayerLimit + maxHumanPlayerLimit >= MIN_TOTAL_PLAYER_LIMIT;
    assert aiPlayerLimit + maxHumanPlayerLimit <= MAX_TOTAL_PLAYER_LIMIT;

    return maxHumanPlayerLimit;
  }

  public static int getMinAiPlayerLimit (final int humanPlayerLimit)
  {
    Arguments.checkLowerInclusiveBound (humanPlayerLimit, MIN_HUMAN_PLAYER_LIMIT, "humanPlayerLimit",
                                        "GameSettings.MIN_HUMAN_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (humanPlayerLimit, MAX_TOTAL_PLAYER_LIMIT, "humanPlayerLimit",
                                        "GameSettings.MAX_HUMAN_PLAYER_LIMIT");

    // @formatter:off
    //
    // Guarantees there will be at least enough total players (human + AI) to meet MIN_TOTAL_PLAYER_LIMIT requirement.
    //
    // if humanPlayerLimit >= MIN_TOTAL_PLAYER_LIMIT (2), then minAiPlayerLimit = MIN_AI_PLAYER_LIMIT (0)
    // if humanPlayerLimit < MIN_TOTAL_PLAYER_LIMIT (2), then minAiPlayerLimit = MIN_TOTAL_PLAYER_LIMIT (2) - humanPlayerLimit (0 or 1)
    //
    // More specifically:
    //
    // humanPlayerLimit  = 0, minAiPlayerLimit = 2 (total min players:    2)
    // humanPlayerLimit  = 1, minAiPlayerLimit = 1 (total min players:    2)
    // humanPlayerLimit >= 2, minAiPlayerLimit = 0 (total min players: >= 2)
    //
    // @formatter:on
    final int minAiPlayerLimit = Math.max (MIN_TOTAL_PLAYER_LIMIT - humanPlayerLimit, MIN_AI_PLAYER_LIMIT);

    // Sanity checks.
    assert humanPlayerLimit + minAiPlayerLimit >= MIN_TOTAL_PLAYER_LIMIT;
    assert humanPlayerLimit + minAiPlayerLimit <= MAX_TOTAL_PLAYER_LIMIT;

    return minAiPlayerLimit;
  }

  public static int getMaxAiPlayerLimit (final int humanPlayerLimit)
  {
    Arguments.checkLowerInclusiveBound (humanPlayerLimit, MIN_HUMAN_PLAYER_LIMIT, "humanPlayerLimit",
                                        "GameSettings.MIN_HUMAN_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (humanPlayerLimit, MAX_TOTAL_PLAYER_LIMIT, "humanPlayerLimit",
                                        "GameSettings.MAX_HUMAN_PLAYER_LIMIT");

    // @formatter:off
    //
    // Guarantees there will be at most total players (human + AI) to meet MAX_TOTAL_PLAYER_LIMIT requirement.
    //
    // More specifically:
    //
    // humanPlayerLimit = 10, maxAiPlayerLimit =  0 (total max players: 10)
    // humanPlayerLimit =  9, maxAiPlayerLimit =  1 (total max players: 10)
    // humanPlayerLimit =  8, maxAiPlayerLimit =  2 (total max players: 10)
    // ...
    // humanPlayerLimit =  0, maxAiPlayerLimit = 10 (total max players: 10)
    //
    // @formatter:on
    final int maxAiPlayerLimit = MAX_TOTAL_PLAYER_LIMIT - humanPlayerLimit;

    // Sanity checks.
    assert humanPlayerLimit + maxAiPlayerLimit >= MIN_TOTAL_PLAYER_LIMIT;
    assert humanPlayerLimit + maxAiPlayerLimit <= MAX_TOTAL_PLAYER_LIMIT;

    return maxAiPlayerLimit;
  }

  public static Builder builder ()
  {
    return new Builder ();
  }

  public static ClassicGameRules defaults ()
  {
    return builder ().build ();
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
  private static int calculateInitialArmies (final int totalPlayerLimit)
  {
    return totalPlayerLimit < 10 ? 40 - 5 * (totalPlayerLimit - 2) : 5;
  }

  private static int calculateMinWinPercentage (final int totalPlayerLimit, final int totalCountryCount)
  {
    // @formatter:off
    // If country distribution does not divide evenly, some players will receive at most one extra country.
    // This will correctly calculate the maximum number of countries any player will be initially distributed, even in the case of uneven distribution.
    final int maxCountriesAnyPlayerWillBeDistributed = (int) Math.ceil (totalCountryCount / (double) totalPlayerLimit);
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

  private ClassicGameRules (final PersonLimits personLimits,
                            final int winPercentage,
                            final int totalCountryCount,
                            final InitialCountryAssignment initialCountryAssignment)
  {
    Arguments.checkIsNotNull (personLimits, "personLimits");

    final int humanPlayerLimit = personLimits.getPlayerLimitFor (PersonSentience.HUMAN);
    final int aiPlayerLimit = personLimits.getPlayerLimitFor (PersonSentience.AI);
    final int spectatorLimit = personLimits.getSpectatorLimit ();

    // @formatter:off
    Arguments.checkLowerInclusiveBound (humanPlayerLimit, MIN_HUMAN_PLAYER_LIMIT, "humanPlayerLimit", "ClassicGameRules.MIN_HUMAN_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (humanPlayerLimit, MAX_HUMAN_PLAYER_LIMIT, "humanPlayerLimit", "ClassicGameRules.MAX_HUMAN_PLAYER_LIMIT");
    Arguments.checkLowerInclusiveBound (aiPlayerLimit, MIN_AI_PLAYER_LIMIT, "aiPlayerLimit", "ClassicGameRules.MIN_AI_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (aiPlayerLimit, MAX_AI_PLAYER_LIMIT, "aiPlayerLimit", "ClassicGameRules.MAX_AI_PLAYER_LIMIT");
    Arguments.checkLowerInclusiveBound (humanPlayerLimit + aiPlayerLimit, MIN_TOTAL_PLAYER_LIMIT, "humanPlayerLimit + aiPlayerLimit", "ClassicGameRules.MIN_TOTAL_PLAYER_LIMIT");
    Arguments.checkUpperInclusiveBound (humanPlayerLimit + aiPlayerLimit, MAX_TOTAL_PLAYER_LIMIT, "humanPlayerLimit + aiPlayerLimit", "ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT");
    Arguments.checkLowerInclusiveBound (spectatorLimit, MIN_SPECTATOR_LIMIT, "spectatorLimit", "ClassicGameRules.MIN_SPECTATOR_LIMIT");
    Arguments.checkUpperInclusiveBound (spectatorLimit, MAX_SPECTATOR_LIMIT, "spectatorLimit", "ClassicGameRules.MAX_SPECTATOR_LIMIT");
    Arguments.checkUpperInclusiveBound (winPercentage, MAX_WIN_PERCENTAGE, "winPercentage", "ClassicGameRules.MAX_WIN_PERCENTAGE");
    Arguments.checkLowerInclusiveBound (totalCountryCount, MIN_TOTAL_COUNTRY_COUNT, "totalCountryCount", "ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT");
    Arguments.checkUpperInclusiveBound (totalCountryCount, MAX_TOTAL_COUNTRY_COUNT, "totalCountryCount", "ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT");
    Arguments.checkIsNotNull (initialCountryAssignment, "initialCountryAssignment");
    // @formatter:on

    minWinPercentage = calculateMinWinPercentage (personLimits.getTotalPlayerLimit (), totalCountryCount);

    Arguments.checkLowerInclusiveBound (winPercentage, minWinPercentage, "winPercentage");

    this.personLimits = personLimits;
    this.winPercentage = winPercentage;
    this.totalCountryCount = totalCountryCount;
    this.initialCountryAssignment = initialCountryAssignment;
    initialArmies = calculateInitialArmies (personLimits.getTotalPlayerLimit ());
    winningCountryCount = calculateWinningCountryCount (winPercentage, totalCountryCount);
  }

  public static final class Builder
  {
    private final PersonLimits.Builder personLimitsBuilder = PersonLimits.builder ().classicModeDefaults ();
    private int winPercentage = DEFAULT_WIN_PERCENTAGE;
    private int totalCountryCount = DEFAULT_TOTAL_COUNTRY_COUNT;
    private InitialCountryAssignment initialCountryAssignment = DEFAULT_INITIAL_COUNTRY_ASSIGNMENT;

    public ClassicGameRules build ()
    {
      return new ClassicGameRules (personLimitsBuilder.build (), winPercentage, totalCountryCount,
              initialCountryAssignment);
    }

    public Builder initialCountryAssignment (@Nullable final InitialCountryAssignment initialCountryAssignment)
    {
      if (initialCountryAssignment == null) return this;

      this.initialCountryAssignment = initialCountryAssignment;

      return this;
    }

    public Builder personLimits (final PersonLimits personLimits)
    {
      Arguments.checkIsNotNull (personLimits, "personLimits");

      personLimitsBuilder.personLimits (personLimits);

      return this;
    }

    public Builder humanPlayerLimit (@Nullable final Integer humanPlayerLimit)
    {
      if (humanPlayerLimit == null) return this;

      personLimitsBuilder.humanPlayers (humanPlayerLimit);

      return this;
    }

    public Builder maxHumanPlayers ()
    {
      return humanPlayerLimit (MAX_HUMAN_PLAYER_LIMIT);
    }

    public Builder defaultHumanPlayers ()
    {
      return humanPlayerLimit (DEFAULT_HUMAN_PLAYER_LIMIT);
    }

    public Builder minHumanPlayers ()
    {
      return humanPlayerLimit (MIN_HUMAN_PLAYER_LIMIT);
    }

    public Builder aiPlayerLimit (@Nullable final Integer aiPlayerLimit)
    {
      if (aiPlayerLimit == null) return this;

      personLimitsBuilder.aiPlayers (aiPlayerLimit);

      return this;
    }

    public Builder maxAiPlayers ()
    {
      return aiPlayerLimit (MAX_AI_PLAYER_LIMIT);
    }

    public Builder defaultAiPlayers ()
    {
      return aiPlayerLimit (DEFAULT_AI_PLAYER_LIMIT);
    }

    public Builder minAiPlayers ()
    {
      return aiPlayerLimit (MIN_AI_PLAYER_LIMIT);
    }

    public Builder spectatorLimit (@Nullable final Integer spectatorLimit)
    {
      if (spectatorLimit == null) return this;

      personLimitsBuilder.spectators (spectatorLimit);

      return this;
    }

    public Builder maxSpectators ()
    {
      return spectatorLimit (MAX_SPECTATOR_LIMIT);
    }

    public Builder defaultSpectators ()
    {
      return spectatorLimit (DEFAULT_SPECTATOR_LIMIT);
    }

    public Builder minSpectators ()
    {
      return spectatorLimit (MIN_SPECTATOR_LIMIT);
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

    /**
     * Use static {@link #builder()} convenience method.
     */
    private Builder ()
    {
    }
  }
}
