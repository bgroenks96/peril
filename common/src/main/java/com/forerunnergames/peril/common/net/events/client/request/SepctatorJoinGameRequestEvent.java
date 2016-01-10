package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

public final class SepctatorJoinGameRequestEvent implements ClientRequestEvent
{
  private final String name;

  public SepctatorJoinGameRequestEvent (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    this.name = name;
  }

  public String getSpectatorName ()
  {
    return name;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {}", getClass ().getSimpleName (), name);
  }

  @RequiredForNetworkSerialization
  private SepctatorJoinGameRequestEvent ()
  {
    name = null;
  }
}
