package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameDeniedEvent implements LocalEvent
{
  private final CreateGameRequestEvent requestEvent;
  private final String reasonForDenial;

  public CreateGameDeniedEvent (final CreateGameRequestEvent requestEvent, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    this.requestEvent = requestEvent;
    this.reasonForDenial = reasonForDenial;
  }

  public String getReasonForDenial ()
  {
    return reasonForDenial;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return requestEvent.getGameServerConfiguration ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Original request: {} | Reason for Denial: {}", getClass ().getSimpleName (),
                           requestEvent, reasonForDenial);
  }
}
