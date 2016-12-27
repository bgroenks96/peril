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

package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import com.google.common.collect.ImmutableSet;

public interface CoreCommunicator
{
  ImmutableSet <PlayerPacket> fetchCurrentPlayerData ();

  void requestSendGameStateTo (PlayerPacket player);

  <T extends PlayerRequestEvent> void publishPlayerRequestEvent (final PlayerPacket player, final T requestEvent);

  <T extends ResponseRequestEvent, R extends PlayerInputRequestEvent> void publishPlayerResponseRequestEvent (final PlayerPacket player,
                                                                                                              final T responseRequestEvent,
                                                                                                              final R inputRequestEvent);

  <T extends InformRequestEvent, R extends PlayerInformEvent> void publishPlayerInformRequestEvent (final PlayerPacket player,
                                                                                                    final T informRequestEvent,
                                                                                                    final R informEvent);
}
