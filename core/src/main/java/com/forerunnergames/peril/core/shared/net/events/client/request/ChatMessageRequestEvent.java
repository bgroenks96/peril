package com.forerunnergames.peril.core.shared.net.events.client.request;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.RequestEvent;

import javax.annotation.Nullable;

public final class ChatMessageRequestEvent implements ChatMessageEvent, RequestEvent
{
  private final ChatMessageEvent event;

  public ChatMessageRequestEvent (final ChatMessage chatMessage)
  {
    Arguments.checkIsNotNull (chatMessage, "chatMessage");

    event = new DefaultChatMessageEvent (chatMessage);
  }

  @Nullable
  @Override
  public Author getAuthor ()
  {
    return event.getAuthor ();
  }

  @Override
  public boolean hasAuthor ()
  {
    return event.hasAuthor ();
  }

  @Override
  public ChatMessage getMessage ()
  {
    return event.getMessage ();
  }

  @Override
  public String getMessageText ()
  {
    return event.getMessageText ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", ((Object) this).getClass ().getSimpleName (), event);
  }

  @RequiredForNetworkSerialization
  private ChatMessageRequestEvent ()
  {
    event = null;
  }
}