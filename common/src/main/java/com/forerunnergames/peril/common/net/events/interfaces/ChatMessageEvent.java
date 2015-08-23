package com.forerunnergames.peril.common.net.events.interfaces;

import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Author;

import javax.annotation.Nullable;

public interface ChatMessageEvent extends MessageEvent <ChatMessage>
{
  @Nullable
  Author getAuthor ();

  boolean hasAuthor ();
}
