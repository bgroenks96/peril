package com.forerunnergames.peril.client.messages;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;

public final class DefaultStatusMessage implements StatusMessage
{
  private final Message message;

  public DefaultStatusMessage (final String messageText)
  {
    Arguments.checkIsNotNull (messageText, "messageText");

    message = new DefaultMessage (messageText);
  }

  @Override
  public String getText ()
  {
    return message.getText ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Message: %2$s", getClass ().getSimpleName (), message);
  }
}