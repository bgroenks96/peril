package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public final class PlayerAttackVictoryEvent extends AbstractPlayerEvent implements ServerNotificationEvent
{
  final BattleResultPacket battleResult;

  public PlayerAttackVictoryEvent (final BattleResultPacket battleResult)
  {
    Arguments.checkIsNotNull (battleResult, "battleResult");

    this.battleResult = battleResult;
  }

  public BattleResultPacket getBattleResult ()
  {
    return this.battleResult;
  }

  @RequiredForNetworkSerialization
  private PlayerAttackVictoryEvent ()
  {
    battleResult = null;
  }
}
