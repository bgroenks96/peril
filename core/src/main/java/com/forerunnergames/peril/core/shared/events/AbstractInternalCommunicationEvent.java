package com.forerunnergames.peril.core.shared.events;

import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

public abstract class AbstractInternalCommunicationEvent implements InternalCommunicationEvent
{
  private final Id eventId = IdGenerator.generateUniqueId ();

  @Override
  public final Id getEventId ()
  {
    return eventId;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Id: {}", getClass ().getSimpleName (), eventId.toString ());
  }
}
