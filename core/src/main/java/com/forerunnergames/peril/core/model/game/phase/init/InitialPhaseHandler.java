package com.forerunnergames.peril.core.model.game.phase.init;

import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.core.model.game.phase.GamePhaseHandler;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;

public interface InitialPhaseHandler extends GamePhaseHandler
{
  @StateEntryAction
  void determinePlayerTurnOrder ();

  @StateEntryAction
  void distributeInitialArmies ();

  @StateEntryAction
  void waitForCountryAssignmentToBegin ();

  @StateEntryAction
  void randomlyAssignPlayerCountries ();

  @StateEntryAction
  void beginInitialReinforcementPhase ();

  @StateEntryAction
  void waitForPlayersToClaimInitialCountries ();

  @StateTransitionCondition
  boolean verifyPlayerClaimCountryResponseRequest (final PlayerClaimCountryResponseRequestEvent event);

  @StateEntryAction
  void waitForPlayersToReinforceInitialCountries ();

  @StateTransitionCondition
  boolean verifyPlayerInitialCountryReinforcements (final PlayerReinforceCountryRequestEvent event);

  void advancePlayerTurn ();

  void resetTurn ();
}
