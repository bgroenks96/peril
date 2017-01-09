/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.forerunnergames.peril.common.net.events.client.request.CommandMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.defaults.AbstractMessageEvent;
import com.forerunnergames.peril.common.net.events.interfaces.CommandMessageEvent;
import com.forerunnergames.peril.common.net.messages.CommandMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class CommandMessageDeniedEvent extends AbstractMessageEvent <CommandMessage>
        implements CommandMessageEvent, DeniedEvent <CommandMessageRequestEvent, String>
{
  private final CommandMessageRequestEvent deniedRequest;
  private final String reason;

  public CommandMessageDeniedEvent (final CommandMessage message,
                                    final CommandMessageRequestEvent deniedRequest,
                                    final String reason)
  {
    super (message);

    Arguments.checkIsNotNull (deniedRequest, "deniedRequest");
    Arguments.checkIsNotNull (reason, "reason");

    this.deniedRequest = deniedRequest;
    this.reason = reason;
  }

  @Override
  public CommandMessageRequestEvent getDeniedRequest ()
  {
    return deniedRequest;
  }

  @Override
  public String getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DeniedRequest: [{}] | Reason: {}", super.toString (), deniedRequest, reason);
  }

  @RequiredForNetworkSerialization
  private CommandMessageDeniedEvent ()
  {
    deniedRequest = null;
    reason = null;
  }
}
