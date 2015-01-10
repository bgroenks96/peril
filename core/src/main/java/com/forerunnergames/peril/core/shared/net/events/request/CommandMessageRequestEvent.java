package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultCommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.CommandMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class CommandMessageRequestEvent implements CommandMessageEvent, RequestEvent
{
  private final CommandMessageEvent event;

  public CommandMessageRequestEvent (final CommandMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    event = new DefaultCommandMessageEvent (message);
  }

  @Override
  public CommandMessage getMessage()
  {
    return event.getMessage();
  }

  @Override
  public String getMessageText()
  {
    return event.getMessageText();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), event);
  }

  @RequiredForNetworkSerialization
  private CommandMessageRequestEvent()
  {
    event = null;
  }
}
