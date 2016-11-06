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

package com.forerunnergames.peril.ai.net;

import com.forerunnergames.peril.common.AbstractJoinGameServerHandler;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class AiJoinGameServerHandler extends AbstractJoinGameServerHandler
{
  public AiJoinGameServerHandler (final MBassador <Event> internalEventBus)
  {
    super (internalEventBus);
  }

  @Override
  protected boolean isSelf (final JoinGameServerSuccessEvent event, final String playerName)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (playerName, "playerName");

    return event.getClientAddress ().equals (playerName);
  }
}
