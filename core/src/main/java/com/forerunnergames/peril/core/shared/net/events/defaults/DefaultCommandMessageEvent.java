package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.CommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.CommandMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCommandMessageEvent implements CommandMessageEvent
{
  private final CommandMessage message;

  public DefaultCommandMessageEvent (final CommandMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @Override
  public CommandMessage getMessage ()
  {
    return message;
  }

  @Override
  public String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), message);
  }

  @RequiredForNetworkSerialization
  private DefaultCommandMessageEvent ()
  {
    message = null;
  }
}
