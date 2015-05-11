package com.forerunnergames.peril.core.shared.net.events.server.defaults;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.GameNotificationEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultStatusMessageEvent implements StatusMessageEvent, GameNotificationEvent
{
  private final StatusMessage message;

  public DefaultStatusMessageEvent (final StatusMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @Override
  public StatusMessage getMessage ()
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
    return String.format ("%1$s: Message: %2$s", ((Object) this).getClass ().getSimpleName (), message);
  }

  @RequiredForNetworkSerialization
  private DefaultStatusMessageEvent ()
  {
    message = null;
  }
}
