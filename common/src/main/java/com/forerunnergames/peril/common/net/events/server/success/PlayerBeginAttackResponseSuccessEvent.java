package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerBeginAttackResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent
{
  private final PendingBattleActorPacket attacker;
  private final PendingBattleActorPacket defender;
  private final int minAttackerDieCount;
  private final int maxAttackerDieCount;

  public PlayerBeginAttackResponseSuccessEvent (final PendingBattleActorPacket attacker,
                                                final PendingBattleActorPacket defender,
                                                final int minAttackerDieCount,
                                                final int maxAttackerDieCount)
  {
    super (attacker.getPlayer ());

    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");
    Arguments.checkIsNotNegative (minAttackerDieCount, "minAttackerDieCount");
    Arguments.checkIsNotNegative (maxAttackerDieCount, "maxAttackerDieCount");

    this.attacker = attacker;
    this.defender = defender;
    this.minAttackerDieCount = minAttackerDieCount;
    this.maxAttackerDieCount = maxAttackerDieCount;
  }

  public PendingBattleActorPacket getAttacker ()
  {
    return attacker;
  }

  public PendingBattleActorPacket getDefender ()
  {
    return defender;
  }

  public int getMinAttackerDieCount ()
  {
    return minAttackerDieCount;
  }

  public int getMaxAttackerDieCount ()
  {
    return maxAttackerDieCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{} | Attacker: [{}] | Defender: [{}] | MinAttackerDieCount: [{}] | MaxAttackerDieCount: [{}]",
                           super.toString (), attacker, defender, minAttackerDieCount, maxAttackerDieCount);
  }

  @RequiredForNetworkSerialization
  private PlayerBeginAttackResponseSuccessEvent ()
  {
    attacker = null;
    defender = null;
    minAttackerDieCount = 0;
    maxAttackerDieCount = 0;
  }
}
