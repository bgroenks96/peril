package com.forerunnergames.peril.common.game;

public enum DieOutcome
{
  WIN,
  LOSE,
  NONE;

  public String lowerCaseName ()
  {
    return name ().toLowerCase ();
  }
}
