package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultStatusMessageEvent implements StatusMessageEvent
{
  private final StatusMessage message;

  public DefaultStatusMessageEvent (final StatusMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @Override
  public StatusMessage getMessage()
  {
    return message;
  }

  @Override
  public String getMessageText()
  {
    return message.getText();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Message: %2$s", getClass().getSimpleName(), message);
  }

  @RequiredForNetworkSerialization
  private DefaultStatusMessageEvent()
  {
    message = null;
  }
}
