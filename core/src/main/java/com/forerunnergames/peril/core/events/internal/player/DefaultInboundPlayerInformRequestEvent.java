/*
 * Copyright © 2016 Forerunner Games, LLC.
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

import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.tools.common.Arguments;

public class DefaultInboundPlayerInformRequestEvent <T extends InformRequestEvent, R extends PlayerInformEvent>
        extends AbstractInternalCommunicationEvent implements InboundPlayerInformRequestEvent <T, R>
{
  private final PlayerPacket player;
  private final T informRequestEvent;
  private final R informEvent;

  public DefaultInboundPlayerInformRequestEvent (final PlayerPacket player,
                                                 final T informRequestEvent,
                                                 final R informEvent)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (informRequestEvent, "informRequestEvent");
    Arguments.checkIsNotNull (informEvent, "informEvent");

    this.player = player;
    this.informRequestEvent = informRequestEvent;
    this.informEvent = informEvent;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public T getRequestEvent ()
  {
    return informRequestEvent;
  }

  @Override
  public R getOriginalInformEvent ()
  {
    return informEvent;
  }
}
