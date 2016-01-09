package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.packets.person.ObserverPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

public final class ObserverJoinGameSuccessEvent implements SuccessEvent
{
  private final ObserverPacket observer;

  public ObserverJoinGameSuccessEvent (final ObserverPacket observer)
  {
    Arguments.checkIsNotNull (observer, "observer");

    this.observer = observer;
  }

  public ObserverPacket getObserver ()
  {
    return observer;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Observer: [{}]", getClass ().getSimpleName (), observer);
  }

  @RequiredForNetworkSerialization
  private ObserverJoinGameSuccessEvent ()
  {
    observer = null;
  }
}
