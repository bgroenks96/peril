package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class ChangePlayerLimitRequestEvent implements RequestEvent
{
  private final int playerLimitDelta;

  public ChangePlayerLimitRequestEvent (final int playerLimitDelta)
  {
    this.playerLimitDelta = playerLimitDelta;
  }

  public int getPlayerLimitDelta()
  {
    return playerLimitDelta;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Player limit delta: %2$s", getClass().getSimpleName(), playerLimitDelta);
  }

  // Required for network serialization
  private ChangePlayerLimitRequestEvent()
  {
    playerLimitDelta = 0;
  }
}
