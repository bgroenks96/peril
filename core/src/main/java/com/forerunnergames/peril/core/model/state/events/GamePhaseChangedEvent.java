package com.forerunnergames.peril.core.model.state.events;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.core.model.game.phase.GamePhaseHandler;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public class GamePhaseChangedEvent implements StateEvent
{
  private final GamePhase currentPhase;
  private final GamePhaseHandler currentHandler;

  public GamePhaseChangedEvent (final GamePhase currentPhase, final GamePhaseHandler currentHandler)
  {
    Arguments.checkIsNotNull (currentPhase, "currentPhase");
    Arguments.checkIsNotNull (currentHandler, "currentHandler");

    this.currentPhase = currentPhase;
    this.currentHandler = currentHandler;
  }

  public GamePhase getCurrentPhase ()
  {
    return currentPhase;
  }

  public GamePhaseHandler getCurrentHandler ()
  {
    return currentHandler;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Current: {} | Handler: {}", getClass ().getSimpleName (), currentPhase,
                           currentHandler.getClass ().getSimpleName ());
  }
}
