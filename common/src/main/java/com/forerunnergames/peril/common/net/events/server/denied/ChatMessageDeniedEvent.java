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

import com.forerunnergames.peril.common.net.events.defaults.AbstractMessageEvent;
import com.forerunnergames.peril.common.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

import javax.annotation.Nullable;

public final class ChatMessageDeniedEvent extends AbstractMessageEvent <ChatMessage>
        implements ChatMessageEvent, DeniedEvent <String>
{
  private ChatMessage message;
  private final String reason;

  public ChatMessageDeniedEvent (final ChatMessage message, final String reason)
  {
    super (message);

    Arguments.checkIsNotNull (reason, "reason");

    this.message = message;
    this.reason = reason;
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
  public String getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Reason: {}", getClass ().getSimpleName (), reason);
  }

  @RequiredForNetworkSerialization
  private ChatMessageDeniedEvent ()
  {
    reason = null;
  }
}
