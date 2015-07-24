package com.forerunnergames.peril.core.shared.event;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public abstract class AbstractInternalResponseEvent extends AbstractInternalCommunicationEvent
        implements InternalResponseEvent
{
  private final Id requestEventId;

  public AbstractInternalResponseEvent (final Id requestEventId)
  {
    Arguments.checkIsNotNull (requestEventId, "requestEventId");

    this.requestEventId = requestEventId;
  }

  @Override
  public Id getRequestEventId ()
  {
    return requestEventId;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Reply to: {}", super.toString (), requestEventId);
  }
}
