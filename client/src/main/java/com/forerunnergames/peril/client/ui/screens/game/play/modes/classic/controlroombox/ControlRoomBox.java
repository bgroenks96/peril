package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

public interface ControlRoomBox
{
  enum Button
  {
    TRADE_IN,
    FORTIFY,
    END_TURN,
    MY_SETTINGS,
    SURRENDER_AND_QUIT
  }

  void pressButton (final Button surrenderAndQuit);

  void disableButton (final Button button);

  void disableButtonForSelf (final Button button, final PlayerPacket player);

  void disableButtonForEveryoneElse (final Button button, final PlayerPacket player);

  void enableButton (final Button button);

  void enableButtonForSelf (final Button button, final PlayerPacket player);

  void enableButtonForEveryoneElse (final Button button, final PlayerPacket player);

  void setSelfPlayer (final PlayerPacket player);

  Actor asActor ();

  void refreshAssets ();
}
