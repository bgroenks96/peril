package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.peril.core.events.internal.interfaces.InternalResponseEvent;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public class SendGameStateResponseEvent extends AbstractInternalCommunicationEvent implements InternalResponseEvent
{
  public enum ResponseCode
  {
    OK,
    PLAYER_NOT_FOUND
  }

  private final ResponseCode responseCode;
  private final Id requestEventId;

  public SendGameStateResponseEvent (final ResponseCode responseCode, final Id requestEventId)
  {
    this.responseCode = responseCode;
    this.requestEventId = requestEventId;
  }

  public ResponseCode getResponse ()
  {
    return responseCode;
  }

  @Override
  public Id getRequestEventId ()
  {
    return requestEventId;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ResponseCode: {} | Reply-to: {}", super.toString (), responseCode, requestEventId);
  }
}
