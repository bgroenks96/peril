package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.ObserverJoinGameDeniedEvent.Reason;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class ObserverJoinGameDeniedEvent extends AbstractDeniedEvent <Reason>
{
  private final String deniedName;

  public enum Reason
  {
    INVALID_NAME,
    DUPLICATE_NAME;
  }

  public ObserverJoinGameDeniedEvent (final String deniedName, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (deniedName, "deniedName");

    this.deniedName = deniedName;
  }

  public String getObserverName ()
  {
    return deniedName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Reason: {}", getClass ().getSimpleName (), deniedName);
  }

  @RequiredForNetworkSerialization
  private ObserverJoinGameDeniedEvent ()
  {
    deniedName = null;
  }
}
