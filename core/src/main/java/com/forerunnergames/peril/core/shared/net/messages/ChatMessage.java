package com.forerunnergames.peril.core.shared.net.messages;

import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Message;

import javax.annotation.Nullable;

public interface ChatMessage extends Message
{
  public boolean hasAuthor();
  @Nullable public Author getAuthor();
}
