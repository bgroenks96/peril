/*
 * Copyright © 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.server.interfaces.GamePhaseNotificationEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractGamePhaseNotificationEvent implements GamePhaseNotificationEvent
{
  private final GamePhase gamePhase;

  protected AbstractGamePhaseNotificationEvent (final GamePhase gamePhase)
  {
    Arguments.checkIsNotNull (gamePhase, "gamePhase");

    this.gamePhase = gamePhase;
  }

  @RequiredForNetworkSerialization
  protected AbstractGamePhaseNotificationEvent ()
  {
    gamePhase = null;
  }

  @Override
  public final GamePhase getGamePhase ()
  {
    return gamePhase;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | GamePhase: [{}]", getClass ().getSimpleName (), gamePhase);
  }
}
