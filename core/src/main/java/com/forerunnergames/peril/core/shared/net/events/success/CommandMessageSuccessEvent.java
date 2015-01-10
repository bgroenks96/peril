package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultCommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.CommandMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

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
    return String.format ("%1$s: %2$s", ((Object) this).getClass ().getSimpleName (), event);
  }

  @RequiredForNetworkSerialization
  private CommandMessageSuccessEvent ()
  {
    event = null;
  }
}
