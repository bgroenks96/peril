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

package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

import com.forerunnergames.peril.client.events.ConnectToServerDeniedEvent;
import com.forerunnergames.peril.client.events.ConnectToServerSuccessEvent;
import com.forerunnergames.peril.common.AbstractJoinGameServerHandler;
import com.forerunnergames.peril.common.JoinGameServerHandler;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

@Listener (references = References.Strong)
public final class HumanJoinGameServerHandler extends AbstractJoinGameServerHandler
{
  public HumanJoinGameServerHandler (final MBassador <Event> eventBus)
  {
    super (eventBus);
  }

  @Override
  protected boolean isSelf (final JoinGameServerSuccessEvent event, final String playerName)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (playerName, "playerName");

    return true; // Human players only ever receive their own JoinGameServerSuccessEvent.
  }

  @Handler
  void onEvent (final ConnectToServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (isJoinGameIsInProgress (),
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);
    log.info ("Successfully connected to server [{}]", event);
    log.info ("Attempting to join game server... [{}]", event);

    getListener ().onConnectToServerSuccess (event.getServerConfiguration ());
    publishAsync (new HumanJoinGameServerRequestEvent ());
  }

  @Handler
  void onEvent (final ConnectToServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (isJoinGameIsInProgress (),
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);
    log.error ("Could not connect to server: [{}]", event);

    shutDown ();
    getListener ().onConnectToServerFailure (event.getServerConfiguration (), event.getReason ());
  }
}
