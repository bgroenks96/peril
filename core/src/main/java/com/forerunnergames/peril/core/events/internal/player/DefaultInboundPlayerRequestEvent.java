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

package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.RequestEvent;

public class DefaultInboundPlayerRequestEvent <T extends RequestEvent> extends AbstractInternalCommunicationEvent
        implements InboundPlayerRequestEvent <T>
{
  private final PlayerPacket player;
  private final T event;

  public DefaultInboundPlayerRequestEvent (final PlayerPacket player, final T event)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (event, "event");

    this.player = player;
    this.event = event;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public T getRequestEvent ()
  {
    return event;
  }

  @RequiredForNetworkSerialization
  private DefaultInboundPlayerRequestEvent ()
  {
    player = null;
    event = null;
  }
}
