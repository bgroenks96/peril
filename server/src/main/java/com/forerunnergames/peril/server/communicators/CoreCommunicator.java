package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;

import com.google.common.collect.ImmutableSet;

public interface CoreCommunicator
{
  ImmutableSet <PlayerPacket> fetchCurrentPlayerData ();
}
