package com.forerunnergames.peril.common.net.events.ipc;

import com.forerunnergames.peril.common.net.events.ipc.interfaces.InterProcessEvent;
import com.forerunnergames.tools.common.Strings;

import java.util.UUID;

public abstract class AbstractInterProcessEvent implements InterProcessEvent
{
  private final UUID uuid = UUID.randomUUID ();

  @Override
  public UUID getUUID ()
  {
    return uuid;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: UUID: {}", getClass ().getSimpleName (), this.uuid);
  }
}
