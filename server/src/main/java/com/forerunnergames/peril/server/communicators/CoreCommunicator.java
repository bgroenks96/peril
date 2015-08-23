package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

import com.google.common.collect.ImmutableSet;

public interface CoreCommunicator
{
  ImmutableSet <PlayerPacket> fetchCurrentPlayerData ();

  void notifyRemovePlayerFromGame (final PlayerPacket player);
}
