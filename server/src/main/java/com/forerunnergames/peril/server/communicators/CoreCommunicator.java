package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import com.google.common.collect.ImmutableSet;

public interface CoreCommunicator
{
  ImmutableSet <PlayerPacket> fetchCurrentPlayerData ();

  void notifyRemovePlayerFromGame (final PlayerPacket player);

  <T extends PlayerRequestEvent> void publishPlayerRequestEvent (final PlayerPacket player, final T requestEvent);

  <T extends ResponseRequestEvent> void publishPlayerResponseRequestEvent (final PlayerPacket player,
                                                                           final T responseRequestEvent);
}
