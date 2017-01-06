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
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerGamePhaseNotificationEvent extends AbstractPlayerEvent
        implements PlayerGamePhaseNotificationEvent
{
  private final GamePhase gamePhase;

  protected AbstractPlayerGamePhaseNotificationEvent (final PlayerPacket player, final GamePhase gamePhase)
  {
    super (player);

    Arguments.checkIsNotNull (gamePhase, "gamePhase");

    this.gamePhase = gamePhase;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerGamePhaseNotificationEvent ()
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
    return Strings.format ("{} | GamePhase: [{}]", super.toString (), gamePhase);
  }
}
