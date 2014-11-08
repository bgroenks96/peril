package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.net.events.RequestEvent;

import javax.annotation.Nullable;

public final class ChatMessageRequestEvent implements ChatMessageEvent, RequestEvent
{
  private final ChatMessageEvent event;

  public ChatMessageRequestEvent (final ChatMessage chatMessage)
  {
    Arguments.checkIsNotNull (chatMessage, "chatMessage");

    event = new DefaultChatMessageEvent (chatMessage);
  }

  @Override
  public ChatMessage getMessage()
  {
    return event.getMessage();
  }

  @Override
  public String getMessageText()
  {
    return event.getMessageText();
  }

  @Override
  public boolean hasAuthor()
  {
    return event.hasAuthor();
  }

  @Nullable
  @Override
  public Author getAuthor()
  {
    return event.getAuthor();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), event);
  }

  // Required for network serialization
  private ChatMessageRequestEvent()
  {
    event = null;
  }
}
