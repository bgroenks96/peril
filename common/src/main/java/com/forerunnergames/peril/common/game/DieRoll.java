package com.forerunnergames.peril.common.game;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DieRoll
{
  private final DieFaceValue dieFaceValue;
  private final DieOutcome dieOutcome;

  public DieRoll (final DieFaceValue dieFaceValue, final DieOutcome dieOutcome)
  {
    Arguments.checkIsNotNull (dieFaceValue, "dieFaceValue");
    Arguments.checkIsNotNull (dieOutcome, "dieOutcome");

    this.dieFaceValue = dieFaceValue;
    this.dieOutcome = dieOutcome;
  }

  public DieFaceValue getDieValue ()
  {
    return dieFaceValue;
  }

  public DieOutcome getOutcome ()
  {
    return dieOutcome;
  }

  @RequiredForNetworkSerialization
  public DieRoll ()
  {
    dieFaceValue = null;
    dieOutcome = null;
  }
}
