package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class BattleOutcome
{
  private String attackingCountryName = "";
  private int attackingCountryDeltaArmies = 0;
  private String attackingPlayerName = "";
  private String defendingCountryName = "";
  private int defendingCountryDeltaArmies = 0;
  private String defendingPlayerName = "";

  public BattleOutcome set (final String attackingCountryName,
                            final int attackingCountryDeltaArmies,
                            final String attackingPlayerName,
                            final String defendingCountryName,
                            final int defendingCountryDeltaArmies,
                            final String defendingPlayerName)
  {
    Arguments.checkIsNotNull (attackingCountryName, "attackingCountryName");
    Arguments.checkIsNotNull (attackingPlayerName, "attackingPlayerName");
    Arguments.checkIsNotNull (defendingCountryName, "defendingCountryName");
    Arguments.checkIsNotNull (defendingPlayerName, "defendingPlayerName");

    this.attackingCountryName = attackingCountryName;
    this.attackingCountryDeltaArmies = attackingCountryDeltaArmies;
    this.attackingPlayerName = attackingPlayerName;
    this.defendingCountryName = defendingCountryName;
    this.defendingCountryDeltaArmies = defendingCountryDeltaArmies;
    this.defendingPlayerName = defendingPlayerName;

    return this;
  }

  public BattleOutcome set (final BattleOutcome outcome)
  {
    Arguments.checkIsNotNull (outcome, "outcome");

    attackingCountryName = outcome.attackingCountryName;
    attackingCountryDeltaArmies = outcome.attackingCountryDeltaArmies;
    attackingPlayerName = outcome.attackingPlayerName;
    defendingCountryName = outcome.defendingCountryName;
    defendingCountryDeltaArmies = outcome.defendingCountryDeltaArmies;
    defendingPlayerName = outcome.defendingPlayerName;

    return this;
  }

  public String getAttackingCountryName ()
  {
    return attackingCountryName;
  }

  public int getAttackingCountryDeltaArmies ()
  {
    return attackingCountryDeltaArmies;
  }

  public String getAttackingPlayerName ()
  {
    return attackingPlayerName;
  }

  public String getDefendingCountryName ()
  {
    return defendingCountryName;
  }

  public int getDefendingCountryDeltaArmies ()
  {
    return defendingCountryDeltaArmies;
  }

  public String getDefendingPlayerName ()
  {
    return defendingPlayerName;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Attacking country name: {} | Attacking country delta armies: {}"
                                   + " | Attacking player name: {} | Defending country name: {}"
                                   + " | Defending country delta armies: {} | Defending player name: {}",
                           getClass ().getSimpleName (), attackingCountryName, attackingCountryDeltaArmies,
                           attackingPlayerName, defendingCountryName, defendingCountryDeltaArmies, defendingPlayerName);
  }
}
