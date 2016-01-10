package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

public final class SpectatorJoinGameSuccessEvent implements SuccessEvent
{
  private final SpectatorPacket spectator;

  public SpectatorJoinGameSuccessEvent (final SpectatorPacket spectator)
  {
    Arguments.checkIsNotNull (spectator, "spectator");

    this.spectator = spectator;
  }

  public SpectatorPacket getSpectator ()
  {
    return spectator;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Spectator: [{}]", getClass ().getSimpleName (), spectator);
  }

  @RequiredForNetworkSerialization
  private SpectatorJoinGameSuccessEvent ()
  {
    spectator = null;
  }
}
