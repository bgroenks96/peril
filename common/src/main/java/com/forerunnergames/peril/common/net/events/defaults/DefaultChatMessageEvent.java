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

package com.forerunnergames.peril.common.net.events.defaults;

import com.forerunnergames.peril.common.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import javax.annotation.Nullable;

public final class DefaultChatMessageEvent implements ChatMessageEvent
{
  private final ChatMessage message;

  public DefaultChatMessageEvent (final ChatMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @Nullable
  @Override
  public Author getAuthor ()
  {
    return message.getAuthor ();
  }

  @Override
  public boolean hasAuthor ()
  {
    return message.hasAuthor ();
  }

  @Override
  public ChatMessage getMessage ()
  {
    return message;
  }

  @Override
  public String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s : Message: %2$s", getClass ().getSimpleName (), message);
  }

  @RequiredForNetworkSerialization
  private DefaultChatMessageEvent ()
  {
    message = null;
  }
}
