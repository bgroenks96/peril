package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.interfaces.MessageEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public abstract class AbstractMessageEvent <T extends Message> implements MessageEvent <T>
{
  private final T message;

  public AbstractMessageEvent (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @Override
  public final T getMessage()
  {
    return message;
  }

  @Override
  public final String getMessageText()
  {
    return message.getText();
  }

  @Override
  public final String toString()
  {
    return String.format ("%1$s", message);
  }

  @RequiredForNetworkSerialization
  protected AbstractMessageEvent()
  {
    message = null;
  }
}