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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.net.packets.person.PersonPacket;

public interface ControlRoomBox
{
  enum Button
  {
    TRADE_IN,
    FORTIFY,
    END_TURN,
    MY_SETTINGS,
    QUIT
  }

  void pressButton (final Button button);

  void disableButton (final Button button);

  void disableButtonForSelf (final Button button, final PersonPacket person);

  void disableButtonForEveryoneElse (final Button button, final PersonPacket person);

  void enableButton (final Button button);

  void enableButtonForSelf (final Button button, final PersonPacket person);

  void enableButtonForEveryoneElse (final Button button, final PersonPacket person);

  void setButtonText (final Button button, final String text);

  void setButtonTextForSelf (final Button button, final PersonPacket person, final String text);

  void setSelf (final PersonPacket person);

  Actor asActor ();

  void refreshAssets ();
}
