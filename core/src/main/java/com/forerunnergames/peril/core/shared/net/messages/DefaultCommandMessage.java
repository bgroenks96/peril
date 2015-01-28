package com.forerunnergames.peril.core.shared.net.messages;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCommandMessage implements CommandMessage
{
  private final Message message;

  public DefaultCommandMessage (final String messageText)
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
    return String.format ("%1$s: Message: %2$s", ((Object) this).getClass ().getSimpleName (), message);
  }

  @RequiredForNetworkSerialization
  private DefaultCommandMessage ()
  {
    message = null;
  }
}
