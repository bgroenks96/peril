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

package com.forerunnergames.peril.core.events;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerAnswerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Used by Core to map inbound events to players.
 */
public interface EventRegistry
{
  /**
   * Registers <code>player</code> as the sender of <code>event</code>.
   */
  void registerTo (PlayerPacket player, Event event);

  /**
   * Retrieves the player who is registered as the sender of <code>event</code> if and only if i) <code>event</event>
   * was sent by a player and ii) event was registered with this {@link EventRegistry}. Otherwise, an absent result is
   * returned.
   */
  Optional <PlayerPacket> playerFor (Event event);

  /**
   * Retrieves all inbound events sent by <code>player</code>.
   */
  ImmutableSet <Event> eventsFor (PlayerPacket player);

  public boolean isSenderOf (final Event event, final PlayerPacket player);

  public boolean isNotSenderOf (final Event event, final PlayerPacket player);

  /**
   * Fetches the PlayerPacket representing the player from whom this client request event was received.
   */
  public Optional <PlayerPacket> senderOf (final Event event);

  /**
   * Fetches the {@link PlayerInputEvent} of type <code>T</code> corresponding to <code>event</event>.
   */
  <T extends PlayerInputEvent> Optional <T> inputEventFor (final PlayerAnswerEvent <T> event,
                                                           final Class <T> inputRequestType);

  /**
   * Retrieves the last outbound event (sent by Core) with the given type.
   */
  <T extends ServerEvent> Optional <T> lastOutboundEventOfType (Class <T> type);

  /**
   * Retrieves the last outbound event (sent by Core) with the given type.
   */
  <T extends ClientEvent> Optional <T> lastInboundEventOfType (Class <T> type);

  /**
   * Clears the event registry for mapping outbound/inbound events and players.
   */
  void clearRegistry ();

  /**
   * Clears the outbound event cache.
   */
  void clearOutboundCache ();

  /**
   * Clears all references held by this {@link EventRegistry} and unsubscribes it from the event bus.
   */
  void shutDown ();
}
