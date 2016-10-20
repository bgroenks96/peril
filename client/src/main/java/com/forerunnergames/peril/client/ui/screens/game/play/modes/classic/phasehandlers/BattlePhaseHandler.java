package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;

public interface BattlePhaseHandler extends GamePhaseHandler
{
  BattlePhaseHandler NULL = new NullBattlePhaseHandler ();

  void onResultAttackerVictorious (final BattleResultPacket result);

  void onResultAttackerDefeated (final BattleResultPacket result);

  void onRetreat ();

  void onEndBattlePhase ();
}
