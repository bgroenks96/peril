package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.server.controllers.ClientSpectatorMapping;
import com.forerunnergames.tools.net.client.ClientCommunicator;

public interface SpectatorCommunicator extends ClientCommunicator
{
  void sendToSpectator (final SpectatorPacket spectator, final Object msg, final ClientSpectatorMapping mapping);

  void sendToAllSpectators (final Object msg, final ClientSpectatorMapping mapping);

  void sendToAllSpectatorsExcept (final SpectatorPacket spectator,
                                  final Object msg,
                                  final ClientSpectatorMapping mapping);
}
