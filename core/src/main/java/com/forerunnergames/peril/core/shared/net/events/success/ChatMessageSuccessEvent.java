package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

import javax.annotation.Nullable;

public final class ChatMessageSuccessEvent implements ChatMessageEvent, SuccessEvent
{
  private final ChatMessageEvent event;

  public ChatMessageSuccessEvent (final ChatMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    event = new DefaultChatMessageEvent (message);
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

  @RequiredForNetworkSerialization
  private ChatMessageSuccessEvent()
  {
    event = null;
  }
}