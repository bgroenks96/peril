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

package com.forerunnergames.peril.core.model.game;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerAnswerEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputInformEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerInputCanceledEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.EventRegistry;
import com.forerunnergames.peril.core.events.internal.player.NotifyPlayerInputTimeoutEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.RequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.InformRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.Map;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Handler class for internal communication events from server
public class InternalCommunicationHandler
{
  private static final Logger log = LoggerFactory.getLogger (InternalCommunicationHandler.class);
  private final EventRegistry eventRegistry;
  private final MBassador <Event> eventBus;
  private final Map <RequestEvent, PlayerPacket> requestEvents = Maps.newHashMap ();
  private final BiMap <PlayerInputEvent, PlayerAnswerEvent <?>> inputToAnswerEvents;

  public InternalCommunicationHandler (final EventRegistry eventRegistry, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventRegistry, "eventRegistry");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventRegistry = eventRegistry;
    this.eventBus = eventBus;

    inputToAnswerEvents = HashBiMap.create ();

    eventRegistry.initialize ();
  }

  public boolean isSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (player, "player");

    final Optional <PlayerPacket> sender = senderOf (event);
    return sender.isPresent () && sender.get ().is (player);
  }

  public boolean isNotSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (player, "player");

    return !isSenderOf (event, player);
  }

  /**
   * Fetches the PlayerPacket representing the player from whom this client request event was received.
   */
  public Optional <PlayerPacket> senderOf (final RequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (requestEvents.get (event));
  }

  /**
   * Fetches the {@link PlayerInputRequestEvent} corresponding to this ResponseRequestEvent.
   */
  public <T extends PlayerInputRequestEvent> Optional <T> inputRequestFor (final PlayerResponseRequestEvent <T> event,
                                                                           final Class <T> inputRequestType)
  {
    Arguments.checkIsNotNull (inputRequestType, "inputRequestType");
    Arguments.checkIsNotNull (event, "event");

    final PlayerInputRequestEvent inputRequestEvent = (PlayerInputRequestEvent) inputToAnswerEvents.inverse ()
            .get (event);
    return Optional.fromNullable (inputRequestType.cast (inputRequestEvent));
  }

  /**
   * Fetches the {@link PlayerInputInformEvent} corresponding to this {@link InformRequestEvent}.
   */
  public <T extends PlayerInputInformEvent> Optional <T> informEventFor (final PlayerInformRequestEvent <?> event,
                                                                         final Class <T> informEventType)
  {
    Arguments.checkIsNotNull (informEventType, "informEventType");
    Arguments.checkIsNotNull (event, "event");

    final PlayerInputRequestEvent inputInformEvent = (PlayerInputRequestEvent) inputToAnswerEvents.inverse ()
            .get (event);
    return Optional.fromNullable (informEventType.cast (inputInformEvent));
  }

  public <T extends PlayerInputEvent> void republishFor (final PlayerAnswerEvent <T> answerEvent)
  {
    final PlayerInputEvent inputEvent = inputToAnswerEvents.inverse ().get (answerEvent);
    if (inputEvent == null)
    {
      log.warn ("No event found to republish for [{}]", answerEvent);
      return;
    }

    eventBus.publish (inputEvent);
  }

  public <T extends ServerEvent> Optional <T> lastOutboundEventOfType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return eventRegistry.lastOutboundEventOfType (type);
  }

  /**
   * This method should be called periodically to avoid stale request events from polluting the map caches.
   */
  public void clearEventCache ()
  {
    log.debug ("Clearing internal event caches [{} RequestEvents] [{} PlayerAnswerEvents].", requestEvents.size (),
               inputToAnswerEvents.size ());

    requestEvents.clear ();
    cancelAndDiscardInputEventCache ();
  }

  // ------ Outbound Event Handlers ------- //

  @Handler
  void onEvent (final PlayerInputEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final PlayerAnswerEvent <?> previousValue = inputToAnswerEvents.forcePut (event, null);
    if (previousValue != null)
    {
      log.warn ("Overwriting previous value for [{}]: {}", event, previousValue);
    }
  }

  // ------ Inbound Event Handlers ------- //

  @Handler (priority = Integer.MAX_VALUE)
  <T extends PlayerInputEvent> void onEvent (final PlayerAnswerEvent <T> event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final ImmutableSet <T> inputEventMatches = allInputEventsOfType (event.getQuestionType ());
    if (!inputEventMatches.isEmpty ())
    {
      log.warn ("Received answer event with no corresponding outbound event! Event: [{}]", event);
      return;
    }

    // TODO this is broken if there are multiple pending input requests for different players
    inputToAnswerEvents.forcePut (Iterables.getFirst (inputEventMatches, null), event);
  }

  @Handler
  void onEvent (final PlayerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Optional <PlayerPacket> playerMaybe = eventRegistry.playerFor (event);
    if (!playerMaybe.isPresent ())
    {
      log.warn ("Received event [{}] with no playing mapping.", event);
      return;
    }

    log.trace ("Event received [{}]", event);

    requestEvents.put (event, playerMaybe.get ());
  }

  @Handler
  void onEvent (final NotifyPlayerInputTimeoutEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    // GameModel will verify whether or not the skip player turn event is valid
    eventBus.publish (new SkipPlayerTurnEvent (event.getPlayer (), SkipPlayerTurnEvent.Reason.PLAYER_INPUT_TIMED_OUT));
  }

  private <T extends PlayerInputEvent> ImmutableSet <T> allInputEventsOfType (final Class <T> inputEventType)
  {
    return ImmutableSet.copyOf (Iterables.filter (inputToAnswerEvents.keySet (), inputEventType));
  }

  private void cancelAndDiscardInputEventCache ()
  {
    for (final PlayerInputEvent next : ImmutableSet.copyOf (inputToAnswerEvents.keySet ()))
    {
      log.info ("Discarding pending input event: [{}]", next);
      inputToAnswerEvents.remove (next);
      eventBus.publish (new PlayerInputCanceledEvent (next));
    }
  }
}
