package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping;
import com.forerunnergames.tools.net.client.ClientCommunicator;

public interface PlayerCommunicator extends ClientCommunicator
{
  void sendToPlayer (final PlayerPacket player, final Object msg, final ClientPlayerMapping mapping);

  void sendToAllPlayers (final Object msg, final ClientPlayerMapping mapping);

  void sendToAllPlayersExcept (final PlayerPacket player, final Object msg, final ClientPlayerMapping mapping);
}
