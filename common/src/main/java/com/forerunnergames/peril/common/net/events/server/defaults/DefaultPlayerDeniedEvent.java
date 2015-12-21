package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class DefaultPlayerDeniedEvent implements PlayerDeniedEvent <String>
{
  private final PlayerPacket player;
  private final DeniedEvent <String> deniedEvent;

  public DefaultPlayerDeniedEvent (final PlayerPacket player, final String reason)
  {
    Arguments.checkIsNotNull (reason, "reason");

    this.player = player;

    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}", getClass ().getSimpleName (), deniedEvent);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerDeniedEvent ()
  {
    player = null;
    deniedEvent = null;
  }
}
