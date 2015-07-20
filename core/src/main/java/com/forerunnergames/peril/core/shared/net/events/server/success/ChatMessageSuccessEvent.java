package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

import javax.annotation.Nullable;

public final class ChatMessageSuccessEvent implements ChatMessageEvent, ServerEvent, SuccessEvent
{
  private final ChatMessageEvent event;

  public ChatMessageSuccessEvent (final ChatMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    event = new DefaultChatMessageEvent (message);
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
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), event);
  }

  @RequiredForNetworkSerialization
  private ChatMessageSuccessEvent ()
  {
    event = null;
  }
}
