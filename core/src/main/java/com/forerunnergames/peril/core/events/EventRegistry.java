package com.forerunnergames.peril.core.events;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Used by Core to map inbound events to players.
 */
public interface EventRegistry
{
  void initialize ();

  void shutDown ();

  void registerTo (PlayerPacket player, Event event);

  Optional <PlayerPacket> playerFor (Event event);

  ImmutableSet <Event> eventsFor (PlayerPacket player);

  <T extends ServerEvent> Optional <T> lastOutboundEventOfType (Class <T> type);

  void clearRegistry ();

  void clearCache ();
}
