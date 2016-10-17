/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;

final class NullGamePhaseHandler implements GamePhaseHandler
{
  @Override
  public void activate ()
  {
  }

  @Override
  public void activateForSelf (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");
  }

  @Override
  public void activateForEveryoneElse (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");
  }

  @Override
  public void deactivate ()
  {
  }

  @Override
  public void deactivateForSelf (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");
  }

  @Override
  public void deactivateForEveryoneElse (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");
  }

  @Override
  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
  }

  @Override
  public void setSelfPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");
  }

  @Override
  public void reset ()
  {
  }
}
