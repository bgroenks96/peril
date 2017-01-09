package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.peril.core.events.internal.interfaces.InternalResponseEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public final class SendGameStateResponseEvent extends AbstractInternalCommunicationEvent
        implements InternalResponseEvent
{
  private final ResponseCode responseCode;
  private final Id requestEventId;

  public enum ResponseCode
  {
    OK,
    PLAYER_NOT_FOUND
  }

  public SendGameStateResponseEvent (final ResponseCode responseCode, final Id requestEventId)
  {
    Arguments.checkIsNotNull (responseCode, "responseCode");
    Arguments.checkIsNotNull (requestEventId, "requestEventId");

    this.responseCode = responseCode;
    this.requestEventId = requestEventId;
  }

  @Override
  public Id getRequestEventId ()
  {
    return requestEventId;
  }

  public ResponseCode getResponse ()
  {
    return responseCode;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ResponseCode: [{}] | RequestEventId: [{}]", super.toString (), responseCode,
                           requestEventId);
  }
}
