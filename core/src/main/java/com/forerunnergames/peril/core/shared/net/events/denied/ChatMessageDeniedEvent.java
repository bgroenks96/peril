package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.DeniedEvent;

import javax.annotation.Nullable;

public final class ChatMessageDeniedEvent implements ChatMessageEvent, DeniedEvent <String>
{
  private final ChatMessageEvent chatMessageEvent;
  private final DeniedEvent <String> deniedEvent;

  public ChatMessageDeniedEvent (final ChatMessage message, final String reason)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (reason, "reason");

    chatMessageEvent = new DefaultChatMessageEvent (message);
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Nullable
  @Override
  public Author getAuthor ()
  {
    return chatMessageEvent.getAuthor ();
  }

  @Override
  public boolean hasAuthor ()
  {
    return chatMessageEvent.hasAuthor ();
  }

  @Override
  public ChatMessage getMessage ()
  {
    return chatMessageEvent.getMessage ();
  }

  @Override
  public String getMessageText ()
  {
    return chatMessageEvent.getMessageText ();
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s | %3$s", ((Object) this).getClass ().getSimpleName (), chatMessageEvent,
                          deniedEvent);
  }

  @RequiredForNetworkSerialization
  private ChatMessageDeniedEvent ()
  {
    chatMessageEvent = null;
    deniedEvent = null;
  }
}
