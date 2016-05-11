package com.forerunnergames.peril.common.game;

public enum InitialArmyReinforcement
{
  MANUAL,
  RANDOM;

  public boolean is (final InitialArmyReinforcement initialArmyReinforcement)
  {
    return this == initialArmyReinforcement;
  }

  public boolean isNot (final InitialArmyReinforcement initialArmyReinforcement)
  {
    return this != initialArmyReinforcement;
  }
}
