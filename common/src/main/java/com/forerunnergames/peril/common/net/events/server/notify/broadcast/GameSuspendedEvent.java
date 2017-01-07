package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractGamePhaseNotificationEvent;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class GameSuspendedEvent extends AbstractGamePhaseNotificationEvent
{
  public enum Reason
  {
    PLAYER_UNAVAILABLE,
    REQUESTED_BY_HOST
  }

  private final Reason reason;

  public GameSuspendedEvent (final Reason reason)
  {
    super (GamePhase.SUSPENDED);

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
  private GameSuspendedEvent ()
  {
    reason = null;
  }
}
