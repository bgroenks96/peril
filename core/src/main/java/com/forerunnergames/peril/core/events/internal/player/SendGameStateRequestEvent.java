package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.peril.core.events.internal.interfaces.InternalRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public class SendGameStateRequestEvent extends AbstractInternalCommunicationEvent implements InternalRequestEvent
{
  private final PlayerPacket targetPlayer;

  public SendGameStateRequestEvent (final PlayerPacket targetPlayer)
  {
    Arguments.checkIsNotNull (targetPlayer, "targetPlayer");

    this.targetPlayer = targetPlayer;
  }

  public PlayerPacket getTargetPlayer ()
  {
    return targetPlayer;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | TargetPlayer: [{}]", super.toString (), targetPlayer);
  }
}
