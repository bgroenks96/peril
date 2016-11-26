package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.net.events.client.request.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.core.model.game.phase.GamePhaseHandler;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTimerDuration;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;

public interface AttackPhaseHandler extends GamePhaseHandler
{
  @StateEntryAction
  void waitForPlayerToSelectAttackVector ();

  @StateTransitionCondition
  boolean verifyPlayerAttackVector (final PlayerSelectAttackVectorRequestEvent event);

  @StateTransitionAction
  void waitForPlayerAttackOrder ();

  @StateTransitionCondition
  boolean verifyPlayerAttackOrder (final PlayerOrderAttackRequestEvent event);

  @StateTimerDuration
  long getBattleResponseTimeoutMs ();

  @StateTransitionCondition
  boolean handleAttackerTimeout ();

  @StateTransitionCondition
  boolean handleDefenderTimeout ();

  @StateTransitionAction
  void processPlayerRetreat (final PlayerOrderRetreatRequestEvent event);

  @StateTransitionAction
  void processPlayerEndAttackPhase (final PlayerEndAttackPhaseRequestEvent event);

  @StateTransitionCondition
  boolean verifyPlayerDefendCountryResponseRequest (final PlayerDefendCountryResponseRequestEvent event);

  @StateEntryAction
  void processBattle ();

  @StateEntryAction
  void waitForPlayerToOccupyCountry ();

  @StateTransitionCondition
  boolean verifyPlayerOccupyCountryResponseRequest (final PlayerOccupyCountryResponseRequestEvent event);
}
