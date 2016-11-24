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
