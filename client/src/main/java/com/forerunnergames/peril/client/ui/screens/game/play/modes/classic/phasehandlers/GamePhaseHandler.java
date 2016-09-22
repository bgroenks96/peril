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

public interface GamePhaseHandler
{
  void activate ();

  void activateForSelf (final PlayerPacket player);

  void activateForEveryoneElse (final PlayerPacket player);

  void deactivate ();

  void deactivateForSelf (final PlayerPacket player);

  void deactivateForEveryoneElse (final PlayerPacket player);

  void setPlayMap (final PlayMap playMap);

  void setSelfPlayer (final PlayerPacket player);

  void reset ();
}
