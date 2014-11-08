package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

import javax.annotation.Nullable;

public final class ChatMessageDeniedEvent implements ChatMessageEvent, DeniedEvent
{
  private final ChatMessageEvent chatMessageEvent;
  private final DeniedEvent deniedEvent;

  public ChatMessageDeniedEvent (final ChatMessage message, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    chatMessageEvent = new DefaultChatMessageEvent (message);
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
  }

  @Override
  public ChatMessage getMessage()
  {
    return chatMessageEvent.getMessage();
  }

  @Override
  public String getMessageText()
  {
    return chatMessageEvent.getMessageText();
  }

  @Override
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  @Override
  public boolean hasAuthor()
  {
    return chatMessageEvent.hasAuthor();
  }

  @Nullable
  @Override
  public Author getAuthor()
  {
    return chatMessageEvent.getAuthor();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s", getClass().getSimpleName(), chatMessageEvent, deniedEvent);
  }

  // Required for network serialization
  private ChatMessageDeniedEvent()
  {
    chatMessageEvent = null;
    deniedEvent = null;
  }
}
