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

package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.common.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

import javax.annotation.Nullable;

public final class ChatMessageRequestEvent implements ChatMessageEvent, ClientRequestEvent
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
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), event);
  }

  @RequiredForNetworkSerialization
  private ChatMessageRequestEvent ()
  {
    event = null;
  }
}
