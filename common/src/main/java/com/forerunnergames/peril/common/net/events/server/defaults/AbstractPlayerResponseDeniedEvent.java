package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerResponseDeniedEvent <T extends PlayerResponseRequestEvent <?>, R>
        extends AbstractPlayerDeniedEvent <T, R> implements PlayerResponseDeniedEvent <T, R>
{
  protected AbstractPlayerResponseDeniedEvent (final PlayerPacket player, final T deniedRequest, final R reason)
  {
    super (player, deniedRequest, reason);
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerResponseDeniedEvent ()
  {
  }
}
