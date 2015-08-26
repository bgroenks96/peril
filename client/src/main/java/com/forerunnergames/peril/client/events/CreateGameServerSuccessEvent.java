package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameServerSuccessEvent implements LocalEvent
{
  private final CreateGameServerRequestEvent originalRequestEvent;

  public CreateGameServerSuccessEvent (final CreateGameServerRequestEvent originalRequestEvent)
  {
    Arguments.checkIsNotNull (originalRequestEvent, "originalRequestEvent");

    this.originalRequestEvent = originalRequestEvent;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return originalRequestEvent.getGameServerConfiguration ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Original Request Event: {}", getClass ().getSimpleName (), originalRequestEvent);
  }
}
