package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.tools.common.net.events.SuccessEvent;

public final class ChangePlayerLimitSuccessEvent implements SuccessEvent
{
  private final int playerLimitDelta;

  public ChangePlayerLimitSuccessEvent (final int playerLimitDelta)
  {
    this.playerLimitDelta = playerLimitDelta;
  }

  public final int getPlayerLimitDelta()
  {
    return playerLimitDelta;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Player limit delta: %2$s", getClass().getSimpleName(), playerLimitDelta);
  }

  // Required for network serialization
  private ChangePlayerLimitSuccessEvent()
  {
    playerLimitDelta = 0;
  }
}
