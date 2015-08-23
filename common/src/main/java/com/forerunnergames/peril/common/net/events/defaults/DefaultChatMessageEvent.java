package com.forerunnergames.peril.common.net.events.defaults;

import com.forerunnergames.peril.common.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import javax.annotation.Nullable;

public final class DefaultChatMessageEvent implements ChatMessageEvent
{
  private final ChatMessage message;

  public DefaultChatMessageEvent (final ChatMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @Nullable
  @Override
  public Author getAuthor ()
  {
    return message.getAuthor ();
  }

  @Override
  public boolean hasAuthor ()
  {
    return message.hasAuthor ();
  }

  @Override
  public ChatMessage getMessage ()
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
    return String.format ("%1$s : Message: %2$s", getClass ().getSimpleName (), message);
  }

  @RequiredForNetworkSerialization
  private DefaultChatMessageEvent ()
  {
    message = null;
  }
}
