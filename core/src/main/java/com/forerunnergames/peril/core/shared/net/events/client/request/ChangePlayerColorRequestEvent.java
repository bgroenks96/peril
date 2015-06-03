package com.forerunnergames.peril.core.shared.net.events.client.request;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.RequestEvent;

public final class ChangePlayerColorRequestEvent implements PlayerColorEvent, RequestEvent
{
  private final PlayerColorEvent playerColorEvent;

  public ChangePlayerColorRequestEvent (final PlayerColor requestedColor)
  {
    Arguments.checkIsNotNull (requestedColor, "requestedColor");

    playerColorEvent = new DefaultPlayerColorEvent (requestedColor);
  }

  @Override
  public PlayerColor getRequestedColor ()
  {
    return playerColorEvent.getRequestedColor ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), playerColorEvent);
  }

  @RequiredForNetworkSerialization
  private ChangePlayerColorRequestEvent ()
  {
    playerColorEvent = null;
  }
}
