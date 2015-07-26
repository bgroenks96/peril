package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameSuccessEvent implements LocalEvent
{
  private final CreateGameRequestEvent originalRequestEvent;

  public CreateGameSuccessEvent (final CreateGameRequestEvent originalRequestEvent)
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
