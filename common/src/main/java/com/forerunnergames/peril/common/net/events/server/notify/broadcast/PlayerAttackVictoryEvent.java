package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractAttackResultEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerAttackVictoryEvent extends AbstractAttackResultEvent
{
  public PlayerAttackVictoryEvent (final PlayerPacket player, final BattleResultPacket battleResult)
  {
    super (player, battleResult);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackVictoryEvent ()
  {
  }
}
