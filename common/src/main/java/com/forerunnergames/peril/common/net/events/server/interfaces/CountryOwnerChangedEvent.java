package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

import com.google.common.base.Optional;

public interface CountryOwnerChangedEvent extends CountryNotificationEvent
{
  Optional <PlayerPacket> getPreviousOwner ();

  PlayerPacket getNewOwner ();
}
