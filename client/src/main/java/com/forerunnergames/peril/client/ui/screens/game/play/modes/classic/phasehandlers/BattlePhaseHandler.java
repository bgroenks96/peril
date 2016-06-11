package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;

public interface BattlePhaseHandler
{
  void onBattle ();

  void onRetreat ();

  void onEndBattlePhase ();

  void reset ();

  void softReset ();

  void setPlayMap (final PlayMap playMap);
}
