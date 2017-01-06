package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSkipGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class SkipReinforcementPhaseEvent extends AbstractPlayerGamePhaseNotificationEvent
        implements PlayerSkipGamePhaseNotificationEvent
{
  private final Reason reason;

  public enum Reason
  {
    COUNTRY_ARMY_OVERFLOW
  }

  public SkipReinforcementPhaseEvent (final PlayerPacket player, final Reason reason)
  {
    super (player, GamePhase.REINFORCEMENT);

    Arguments.checkIsNotNull (reason, "reason");

    this.reason = reason;
  }

  public Reason getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Reason: {}", super.toString (), reason);
  }

  @RequiredForNetworkSerialization
  private SkipReinforcementPhaseEvent ()
  {
    reason = null;
  }
}
