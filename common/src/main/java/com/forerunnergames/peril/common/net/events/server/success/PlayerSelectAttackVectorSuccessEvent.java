package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectAttackVectorSuccessEvent extends AbstractPlayerEvent implements PlayerSuccessEvent
{
  private final PendingBattleActorPacket attacker;
  private final PendingBattleActorPacket defender;

  public PlayerSelectAttackVectorSuccessEvent (final PendingBattleActorPacket attacker,
                                               final PendingBattleActorPacket defender)
  {
    super (attacker.getPlayer ());

    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");

    this.attacker = attacker;
    this.defender = defender;
  }

  public PendingBattleActorPacket getAttacker ()
  {
    return attacker;
  }

  public PendingBattleActorPacket getDefender ()
  {
    return defender;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Attacker: [{}] | Defender: [{}]", super.toString (), attacker, defender);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectAttackVectorSuccessEvent ()
  {
    attacker = null;
    defender = null;
  }
}
