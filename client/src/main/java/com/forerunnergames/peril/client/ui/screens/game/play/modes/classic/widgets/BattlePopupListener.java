package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;

public interface BattlePopupListener extends PopupListener
{
  void onAttack (final String attackingCountryName, final String defendingCountryName);

  void onRetreat (final String attackingCountryName, final String defendingCountryName);

  void onToggleAutoAttack (final boolean isEnabled);
}
