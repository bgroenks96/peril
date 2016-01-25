/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.common.net.messages;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

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
    return String.format ("%1$s: Author: %2$s Message: %3$s", getClass ().getSimpleName (), author, message);
  }

  @RequiredForNetworkSerialization
  private DefaultChatMessage ()
  {
    author = null;
    message = null;
  }
}
