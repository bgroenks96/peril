package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;

import javax.annotation.Nullable;

public final class DefaultChatMessageEvent implements ChatMessageEvent
{
  private final ChatMessage message;

  public DefaultChatMessageEvent (final ChatMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @Override
  public ChatMessage getMessage()
  {
    return message;
  }

  @Override
  public String getMessageText()
  {
    return message.getText();
  }

  @Override
  public boolean hasAuthor()
  {
    return message.hasAuthor();
  }

  @Nullable
  @Override
  public Author getAuthor()
  {
    return message.getAuthor();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s : Message: %2$s", getClass().getSimpleName(), message);
  }

  // Required for network serialization
  private DefaultChatMessageEvent()
  {
    message = null;
  }
}
