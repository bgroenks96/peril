package com.forerunnergames.peril.core.shared.net.messages;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

import javax.annotation.Nullable;

public final class DefaultChatMessage implements ChatMessage
{
  private final Message message;
  @Nullable
  private final Author author;

  public DefaultChatMessage (final String messageText)
  {
    this (null, messageText);
  }

  public DefaultChatMessage (@Nullable final Author author, final String messageText)
  {
    Arguments.checkIsNotNull (messageText, "messageText");

    this.author = author;
    message = new DefaultMessage (messageText);
  }

  @Nullable
  @Override
  public Author getAuthor ()
  {
    return author;
  }

  @Override
  public boolean hasAuthor ()
  {
    return author != null;
  }

  @Override
  public String getText ()
  {
    return message.getText ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Author: %2$s Message: %3$s", ((Object) this).getClass ().getSimpleName (), author, message);
  }

  @RequiredForNetworkSerialization
  private DefaultChatMessage ()
  {
    author = null;
    message = null;
  }
}
