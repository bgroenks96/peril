package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractGamePhaseNotificationEvent;

public final class GameResumedEvent extends AbstractGamePhaseNotificationEvent
{
  public GameResumedEvent (final GamePhase gamePhase)
  {
    super (gamePhase);
  }
}
