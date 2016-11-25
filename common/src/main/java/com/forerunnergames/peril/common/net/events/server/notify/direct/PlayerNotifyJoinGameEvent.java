package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.DirectPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public class PlayerNotifyJoinGameEvent extends AbstractPlayerEvent implements DirectPlayerEvent
{
  private final UUID playerSecretId;

  public PlayerNotifyJoinGameEvent (final PlayerPacket player, final UUID playerSecretId)
  {
    super (player);

    Arguments.checkIsNotNull (playerSecretId, "playerSecretId");

    this.playerSecretId = playerSecretId;
  }

  public UUID getSecretId ()
  {
    return playerSecretId;
  }

  @RequiredForNetworkSerialization
  private PlayerNotifyJoinGameEvent ()
  {
    this.playerSecretId = null;
  }
}
