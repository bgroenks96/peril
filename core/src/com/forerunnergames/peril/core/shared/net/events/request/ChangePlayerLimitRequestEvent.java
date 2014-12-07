package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerLimitEvent;
import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class ChangePlayerLimitRequestEvent implements RequestEvent, PlayerLimitEvent
{
  private final int playerLimitDelta;

  public ChangePlayerLimitRequestEvent (final int playerLimitDelta)
  {
    this.playerLimitDelta = playerLimitDelta;
  }

  @Override
  public int getPlayerLimitDelta()
  {
    return playerLimitDelta;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Player limit delta: %2$s", getClass().getSimpleName(), playerLimitDelta);
  }

  @RequiredForNetworkSerialization
  private ChangePlayerLimitRequestEvent()
  {
    playerLimitDelta = 0;
  }
}
