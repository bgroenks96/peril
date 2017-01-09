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

package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerFortifyCountryRequestEvent;
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
  boolean verifyPlayerFortifyOrder (final PlayerFortifyCountryRequestEvent event);

  @StateTransitionAction
  boolean verifyPlayerCancelFortifyVector (final PlayerCancelFortifyRequestEvent event);
}
