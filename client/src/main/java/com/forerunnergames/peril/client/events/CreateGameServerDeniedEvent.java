package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameServerDeniedEvent implements LocalEvent
{
  private final CreateGameServerRequestEvent requestEvent;
  private final String reason;

  public CreateGameServerDeniedEvent (final CreateGameServerRequestEvent requestEvent, final String reason)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");
    Arguments.checkIsNotNull (reason, "reason");

    this.requestEvent = requestEvent;
    this.reason = reason;
  }

  public String getReason ()
  {
    return reason;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return requestEvent.getGameServerConfiguration ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Original request: {} | Reason for Denial: {}", getClass ().getSimpleName (),
                           requestEvent, reason);
  }
}
