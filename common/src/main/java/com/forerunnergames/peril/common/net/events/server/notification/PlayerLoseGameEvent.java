package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

/**
 * An event reserved only for the biggest of failures and truest of scrubs.
 */
public final class PlayerLoseGameEvent extends AbstractPlayerEvent implements PlayerNotificationEvent
{
  public PlayerLoseGameEvent (final PlayerPacket player)
  {
    super (player);
  }

  @RequiredForNetworkSerialization
  private PlayerLoseGameEvent ()
  {
  }
}
