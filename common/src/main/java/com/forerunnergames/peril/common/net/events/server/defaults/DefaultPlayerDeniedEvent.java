package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class DefaultPlayerDeniedEvent extends AbstractPlayerEvent implements PlayerDeniedEvent <String>
{
  private final DeniedEvent <String> deniedEvent;

  public DefaultPlayerDeniedEvent (final PlayerPacket player, final String reason)
  {
    super (player);

    Arguments.checkIsNotNull (reason, "reason");

    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | {}", super.toString (), deniedEvent);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerDeniedEvent ()
  {
    deniedEvent = null;
  }
}
