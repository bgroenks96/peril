package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSkipGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class SkipFortifyPhaseEvent extends AbstractPlayerGamePhaseNotificationEvent
        implements PlayerSkipGamePhaseNotificationEvent
{
  public SkipFortifyPhaseEvent (final PlayerPacket player)
  {
    super (player, GamePhase.FORTIFY);
  }

  @RequiredForNetworkSerialization
  private SkipFortifyPhaseEvent ()
  {
  }
}
