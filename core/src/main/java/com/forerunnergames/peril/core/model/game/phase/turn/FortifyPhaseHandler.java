package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerOrderFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.core.model.game.phase.GamePhaseHandler;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;

public interface FortifyPhaseHandler extends GamePhaseHandler
{
  @StateEntryAction
  void waitForPlayerToSelectFortifyVector ();

  @StateTransitionCondition
  boolean verifyPlayerFortifyVectorSelection (final PlayerSelectFortifyVectorRequestEvent event);

  @StateTransitionCondition
  boolean verifyPlayerFortifyOrder (final PlayerOrderFortifyRequestEvent event);

  @StateTransitionAction
  boolean verifyPlayerCancelFortifyVector (final PlayerCancelFortifyRequestEvent event);
}
