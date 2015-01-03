package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultStatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.MessageSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;

public final class StatusMessageSuccessEvent implements StatusMessageEvent, MessageSuccessEvent <StatusMessage>
{
  private final StatusMessageEvent event;

  public StatusMessageSuccessEvent (final StatusMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    event = new DefaultStatusMessageEvent (message);
  }

  @Override
  public StatusMessage getMessage()
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
    return String.format ("%1$s: %2$s ", getClass().getSimpleName(), event);
  }

  @RequiredForNetworkSerialization
  private StatusMessageSuccessEvent()
  {
    event = null;
  }
}
