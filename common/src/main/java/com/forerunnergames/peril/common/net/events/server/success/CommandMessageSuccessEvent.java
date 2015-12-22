package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.defaults.DefaultCommandMessageEvent;
import com.forerunnergames.peril.common.net.events.interfaces.CommandMessageEvent;
import com.forerunnergames.peril.common.net.messages.CommandMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

public final class CommandMessageSuccessEvent implements CommandMessageEvent, SuccessEvent
{
  private final CommandMessageEvent event;

  public CommandMessageSuccessEvent (final CommandMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    event = new DefaultCommandMessageEvent (message);
  }

  @Override
  public CommandMessage getMessage ()
  {
    return event.getMessage ();
  }

  @Override
  public String getMessageText ()
  {
    return event.getMessageText ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), event);
  }

  @RequiredForNetworkSerialization
  private CommandMessageSuccessEvent ()
  {
    event = null;
  }
}
