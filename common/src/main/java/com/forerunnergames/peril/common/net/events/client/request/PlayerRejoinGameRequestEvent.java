package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

import java.util.UUID;

public final class PlayerRejoinGameRequestEvent implements ClientRequestEvent
{
  private final UUID playerSecretId;

  public PlayerRejoinGameRequestEvent (final UUID playerSecretId)
  {
    this.playerSecretId = playerSecretId;
  }

  public UUID getPlayerSecretId ()
  {
    return playerSecretId;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: PlayerSecretId: {}", getClass ().getSimpleName (), playerSecretId);
  }

  @RequiredForNetworkSerialization
  private PlayerRejoinGameRequestEvent ()
  {
    this.playerSecretId = null;
  }
}
