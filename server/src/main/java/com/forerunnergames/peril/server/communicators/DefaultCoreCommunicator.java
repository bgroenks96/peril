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
import com.forerunnergames.peril.core.events.internal.interfaces.InternalRequestEvent;
import com.forerunnergames.peril.core.events.internal.interfaces.InternalResponseEvent;
import com.forerunnergames.peril.core.events.internal.player.DefaultInboundPlayerInformRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.DefaultInboundPlayerRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.DefaultInboundPlayerResponseRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.PlayerDisconnectedEvent;
import com.forerunnergames.peril.core.events.internal.player.UpdatePlayerDataRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.UpdatePlayerDataResponseEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public class DefaultCoreCommunicator implements CoreCommunicator
{
  private final Set <InternalResponseEvent> responses = Sets.newConcurrentHashSet ();
  private final MBassador <Event> eventBus;

  public DefaultCoreCommunicator (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;

    eventBus.subscribe (this);
  }

  @Override
  public ImmutableSet <PlayerPacket> fetchCurrentPlayerData ()
  {
    final UpdatePlayerDataRequestEvent requestEvent = new UpdatePlayerDataRequestEvent ();
    // synchronous publish should guarantee that we receive the response before publish returns
    eventBus.publish (requestEvent);

    // just in case the response somehow was not received (maybe Core sneakily forked a thread or something)...
    // wrap it with Optional to handle null case
    final Optional <InternalResponseEvent> responseEvent = getResponseFor (requestEvent);
    if (!responseEvent.isPresent ()) return ImmutableSet.of ();
    final UpdatePlayerDataResponseEvent playerDataResponse = (UpdatePlayerDataResponseEvent) responseEvent.get ();
    return playerDataResponse.getUpdatedPlayers ();
  }

  @Override
  public void notifyRemovePlayerFromGame (final PlayerPacket player)
  {
    final PlayerDisconnectedEvent leaveGameEvent = new PlayerDisconnectedEvent (player);
    eventBus.publish (leaveGameEvent);
  }

  @Override
  public <T extends PlayerRequestEvent> void publishPlayerRequestEvent (final PlayerPacket player, final T event)
  {
    eventBus.publish (new DefaultInboundPlayerRequestEvent<> (player, event));
  }

  @Override
  public <T extends ResponseRequestEvent, R extends PlayerInputRequestEvent> void publishPlayerResponseRequestEvent (final PlayerPacket player,
                                                                                                                     final T responseRequestEvent,
                                                                                                                     final R inputRequestEvent)
  {
    eventBus.publish (new DefaultInboundPlayerResponseRequestEvent<> (player, responseRequestEvent, inputRequestEvent));
  }

  @Override
  public <T extends InformRequestEvent, R extends PlayerInformEvent> void publishPlayerInformRequestEvent (final PlayerPacket player,
                                                                                                           final T informRequestEvent,
                                                                                                           final R informEvent)
  {
    eventBus.publish (new DefaultInboundPlayerInformRequestEvent<> (player, informRequestEvent, informEvent));
  }

  // --- response handlers --- //

  @Handler
  public void onPlayerDataResponseEvent (final UpdatePlayerDataResponseEvent response)
  {
    Arguments.checkIsNotNull (response, "response");

    responses.add (response);
  }

  private Optional <InternalResponseEvent> getResponseFor (final InternalRequestEvent requestEvent)
  {
    for (final InternalResponseEvent response : responses)
    {
      if (response.getRequestEventId ().is (requestEvent.getEventId ())) return Optional.of (response);
    }

    return Optional.absent ();
  }
}
