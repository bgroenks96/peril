package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerAttackCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public final class PlayerAttackCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final String sourceCountryName;
  private final String targetCountryName;
  private final int attackerDieCount;

  public PlayerAttackCountryResponseRequestEvent (final String sourceCountryName,
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

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerAttackCountryRequestEvent.class;
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryResponseRequestEvent ()
  {
    sourceCountryName = null;
    targetCountryName = null;
    attackerDieCount = 0;
  }
}
