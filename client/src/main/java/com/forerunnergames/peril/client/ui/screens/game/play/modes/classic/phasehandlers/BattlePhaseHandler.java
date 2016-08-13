package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

public interface BattlePhaseHandler
{
  void onBattle ();

  void onResultAttackerVictorious (final BattleResultPacket result);

  void onResultAttackerDefeated (final BattleResultPacket result);

  void onRetreat ();

  void onEndBattlePhase ();

  void reset ();

  void setPlayMap (final PlayMap playMap);

  void setSelfPlayer (final PlayerPacket player);
}
