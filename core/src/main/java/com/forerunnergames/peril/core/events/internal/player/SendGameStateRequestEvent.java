package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.peril.core.events.internal.interfaces.InternalRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class SendGameStateRequestEvent extends AbstractInternalCommunicationEvent implements InternalRequestEvent
{
  private final PlayerPacket targetPlayer;
  private final GameServerConfiguration gameServerConfig;

  public SendGameStateRequestEvent (final PlayerPacket targetPlayer, final GameServerConfiguration gameServerConfig)
  {
    Arguments.checkIsNotNull (targetPlayer, "targetPlayer");
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");

    this.targetPlayer = targetPlayer;
    this.gameServerConfig = gameServerConfig;
  }

  public PlayerPacket getTargetPlayer ()
  {
    return targetPlayer;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return gameServerConfig;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | TargetPlayer: [{}] | GameServerConfig: [{}]", super.toString (), targetPlayer,
                           gameServerConfig);
  }
}
