package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public class PlayerDefendCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final int defenderDieCount;

  public PlayerDefendCountryResponseRequestEvent (final int numDiceToRoll)
  {
    Arguments.checkIsNotNegative (numDiceToRoll, "numDiceToRoll");

    defenderDieCount = numDiceToRoll;
  }

  public int getDefenderDieCount ()
  {
    return defenderDieCount;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerDefendCountryRequestEvent.class;
  }
}
