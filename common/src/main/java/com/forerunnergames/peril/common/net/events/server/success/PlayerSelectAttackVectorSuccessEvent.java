package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractBattleEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectAttackVectorSuccessEvent extends AbstractBattleEvent implements PlayerSuccessEvent
{
  public PlayerSelectAttackVectorSuccessEvent (final PlayerPacket player,
                                               final PendingBattleActorPacket attacker,
                                               final PendingBattleActorPacket defender)
  {
    super (player, attacker, defender);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectAttackVectorSuccessEvent ()
  {
  }
}
