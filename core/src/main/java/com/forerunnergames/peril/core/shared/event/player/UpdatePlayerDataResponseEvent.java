package com.forerunnergames.peril.core.shared.event.player;

import com.forerunnergames.peril.core.shared.event.AbstractInternalResponseEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public class UpdatePlayerDataResponseEvent extends AbstractInternalResponseEvent
{
  final ImmutableSet <PlayerPacket> updatedPlayers;

  public UpdatePlayerDataResponseEvent (final ImmutableSet <PlayerPacket> updatedPlayers, final Id requestEventId)
  {
    super (requestEventId);

    Arguments.checkIsNotNull (updatedPlayers, "updatedPlayers");
    Arguments.checkIsNotNull (requestEventId, "requestEventId");

    this.updatedPlayers = updatedPlayers;
  }

  public ImmutableSet <PlayerPacket> getUpdatedPlayers ()
  {
    return updatedPlayers;
  }
}
