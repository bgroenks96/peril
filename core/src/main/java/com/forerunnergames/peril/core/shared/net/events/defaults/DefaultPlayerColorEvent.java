package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayerColorEvent implements PlayerColorEvent
{
  private final PlayerColor requestedColor;

  public DefaultPlayerColorEvent (final PlayerColor requestedColor)
  {
    Arguments.checkIsNotNull (requestedColor, "requestedColor");

    this.requestedColor = requestedColor;
  }

  @Override
  public PlayerColor getRequestedColor ()
  {
    return requestedColor;
  }

  @Override
  public String toString ()
  {
    return String.format ("Requested color: %1$s", requestedColor);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerColorEvent ()
  {
    requestedColor = null;
  }
}
