package com.forerunnergames.peril.common.net.events.client.request.inform;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerAttackCountryEvent;

public final class PlayerRetreatRequestEvent implements PlayerInformRequestEvent <PlayerAttackCountryEvent>
{
  @Override
  public Class <PlayerAttackCountryEvent> getQuestionType ()
  {
    return PlayerAttackCountryEvent.class;
  }

  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
