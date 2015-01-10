package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Author;

import javax.annotation.Nullable;

public interface ChatMessageEvent extends MessageEvent <ChatMessage>
{
  @Nullable
  public Author getAuthor ();

  public boolean hasAuthor ();
}
