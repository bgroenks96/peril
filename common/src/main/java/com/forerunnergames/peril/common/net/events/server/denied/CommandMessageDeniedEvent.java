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

import com.forerunnergames.peril.common.net.events.defaults.DefaultCommandMessageEvent;
import com.forerunnergames.peril.common.net.events.interfaces.CommandMessageEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.common.net.messages.CommandMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class CommandMessageDeniedEvent implements CommandMessageEvent, DeniedEvent <String>
{
  private final CommandMessageEvent commandMessageEvent;
  private final DeniedEvent <String> deniedEvent;

  public CommandMessageDeniedEvent (final CommandMessage message, final String reason)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (reason, "reason");

    commandMessageEvent = new DefaultCommandMessageEvent (message);
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public CommandMessage getMessage ()
  {
    return commandMessageEvent.getMessage ();
  }

  @Override
  public String getMessageText ()
  {
    return commandMessageEvent.getMessageText ();
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s | %3$s", getClass ().getSimpleName (), commandMessageEvent, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private CommandMessageDeniedEvent ()
  {
    commandMessageEvent = null;
    deniedEvent = null;
  }
}
