/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.core.model.game.phase.init;

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerReinforceCountryRequestEvent;
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
