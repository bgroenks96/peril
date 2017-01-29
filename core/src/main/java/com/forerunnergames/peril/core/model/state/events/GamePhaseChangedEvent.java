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