package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.client.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.colorFrom;

public final class ChangePlayerColorSuccessEvent implements PlayerColorEvent, SuccessEvent
{
  private final PlayerColorEvent playerColorEvent;

  public ChangePlayerColorSuccessEvent (final ChangePlayerColorRequestEvent event)
  {
    this (colorFrom (event));
  }

  public ChangePlayerColorSuccessEvent (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    playerColorEvent = new DefaultPlayerColorEvent (color);
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
  private ChangePlayerColorSuccessEvent ()
  {
    playerColorEvent = null;
  }
}
