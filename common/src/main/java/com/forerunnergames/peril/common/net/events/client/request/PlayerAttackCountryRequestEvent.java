package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerAttackCountryRequestEvent implements PlayerRequestEvent
{
  private final String sourceCountryName;
  private final String targetCountryName;
  private final int attackerDieCount;

  public PlayerAttackCountryRequestEvent (final String sourceCountryName,
                                          final String targetCountryName,
                                          final int attackerDieCount)
  {
    Arguments.checkIsNotNull (sourceCountryName, "sourceCountryName");
    Arguments.checkIsNotNull (targetCountryName, "targetCountryName");
    Arguments.checkIsNotNegative (attackerDieCount, "attackerDieCount");

    this.sourceCountryName = sourceCountryName;
    this.targetCountryName = targetCountryName;
    this.attackerDieCount = attackerDieCount;
  }

  public String getSourceCountryName ()
  {
    return sourceCountryName;
  }

  public String getTargetCounryName ()
  {
    return targetCountryName;
  }

  public int getAttackerDieCount ()
  {
    return attackerDieCount;
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryRequestEvent ()
  {
    sourceCountryName = null;
    targetCountryName = null;
    attackerDieCount = 0;
  }
}
