package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameDeniedEvent implements LocalEvent
{
  private final CreateGameRequestEvent requestEvent;
  private final ClientConfiguration clientConfig;
  private final String reasonForDenial;

  public CreateGameDeniedEvent (final CreateGameRequestEvent requestEvent,
                                final ClientConfiguration clientConfig,
                                final String reasonForDenial)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    this.requestEvent = requestEvent;
    this.clientConfig = clientConfig;
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

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Original request: {} | Client Configuration: {} | Reason for Denial: {}",
                           getClass ().getSimpleName (), requestEvent, clientConfig, reasonForDenial);
  }
}
