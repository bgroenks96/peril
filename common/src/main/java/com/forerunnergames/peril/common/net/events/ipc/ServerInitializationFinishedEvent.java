package com.forerunnergames.peril.common.net.events.ipc;

import com.forerunnergames.peril.common.net.events.ipc.interfaces.ServerInterProcessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class ServerInitializationFinishedEvent extends AbstractInterProcessEvent
        implements ServerInterProcessEvent
{
  private final long initializationTimeMillis;

  public ServerInitializationFinishedEvent (final long initializationTimeMillis)
  {
    Arguments.checkIsNotNegative (initializationTimeMillis, "initializationTimeMillis");

    this.initializationTimeMillis = initializationTimeMillis;
  }

  public long getInitializationTimeMillis ()
  {
    return initializationTimeMillis;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | InitTimeMs: {}", super.toString (), initializationTimeMillis);
  }

  @RequiredForNetworkSerialization
  private ServerInitializationFinishedEvent ()
  {
    initializationTimeMillis = 0;
  }
}
