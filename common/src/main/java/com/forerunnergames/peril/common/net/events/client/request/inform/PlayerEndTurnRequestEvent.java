package com.forerunnergames.peril.common.net.events.client.request.inform;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerEndTurnAvailableEvent;

public final class PlayerEndTurnRequestEvent implements PlayerInformRequestEvent <PlayerEndTurnAvailableEvent>
{
  @Override
  public Class <PlayerEndTurnAvailableEvent> getQuestionType ()
  {
    return PlayerEndTurnAvailableEvent.class;
  }

  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
