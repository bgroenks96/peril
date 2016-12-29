package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.DirectPlayerNotificationEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerInputCanceledEvent extends AbstractPlayerEvent implements DirectPlayerNotificationEvent
{
  private final PlayerInputEvent originalInputEvent;

  public PlayerInputCanceledEvent (final PlayerInputEvent originalInputEvent)
  {
    super (originalInputEvent.getPerson ());

    Arguments.checkIsNotNull (originalInputEvent, "originalInputEvent");

    this.originalInputEvent = originalInputEvent;
  }

  public PlayerInputEvent getOriginalInputEvent ()
  {
    return originalInputEvent;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | OriginalInputEvent: {}", super.toString (), originalInputEvent);
  }

  @RequiredForNetworkSerialization
  private PlayerInputCanceledEvent ()
  {
    this.originalInputEvent = null;
  }
}
