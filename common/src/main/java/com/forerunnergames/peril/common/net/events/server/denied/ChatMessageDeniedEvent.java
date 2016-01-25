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

package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.common.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

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
    return String.format ("%1$s: %2$s | %3$s", getClass ().getSimpleName (), chatMessageEvent, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private ChatMessageDeniedEvent ()
  {
    chatMessageEvent = null;
    deniedEvent = null;
  }
}
