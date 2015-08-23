package com.forerunnergames.peril.common.net.messages;

import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Message;

import javax.annotation.Nullable;

public interface ChatMessage extends Message
{
  @Nullable
  Author getAuthor ();

  boolean hasAuthor ();
}
