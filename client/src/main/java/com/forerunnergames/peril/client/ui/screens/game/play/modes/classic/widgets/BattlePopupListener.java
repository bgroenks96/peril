package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;

public interface BattlePopupListener extends PopupListener
{
  void onBattle ();

  void onAttackerWinFinal ();

  void onAttackerLoseFinal ();
}
