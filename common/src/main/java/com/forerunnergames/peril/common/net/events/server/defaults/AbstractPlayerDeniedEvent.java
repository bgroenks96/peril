package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerDeniedEvent <T extends PlayerRequestEvent, R> extends AbstractPlayerEvent
        implements PlayerDeniedEvent <T, R>
{
  private final T deniedEvent;
  private final R reason;

  public AbstractPlayerDeniedEvent (final PlayerPacket player, final T deniedEvent, final R reason)
  {
    super (player);

    Arguments.checkIsNotNull (deniedEvent, "deniedEvent");
    Arguments.checkIsNotNull (reason, "reason");

    this.deniedEvent = deniedEvent;
    this.reason = reason;
  }

  @Override
  public T getDeniedRequest ()
  {
    return deniedEvent;
  }

  @Override
  public R getReason ()
  {
    return reason;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerDeniedEvent ()
  {
    deniedEvent = null;
    reason = null;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DeniedRequest: {} | Reason: {}", super.toString (), deniedEvent, reason);
  }
}
