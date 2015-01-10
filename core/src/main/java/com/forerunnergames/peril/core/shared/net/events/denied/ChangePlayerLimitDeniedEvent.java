package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerLimitEvent;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public final class ChangePlayerLimitDeniedEvent extends AbstractDeniedEvent <ChangePlayerLimitDeniedEvent.REASON>
        implements PlayerLimitEvent
{
  public enum REASON
  {
    REQUESTED_LIMIT_EQUALS_EXISTING_LIMIT,
    CANNOT_INCREASE_ABOVE_MAX_PLAYERS,
    CANNOT_DECREASE_BELOW_ZERO,
    CANNOT_DECREASE_BELOW_CURRENT_PLAYER_COUNT
  }

  private final int playerLimitDelta;

  public ChangePlayerLimitDeniedEvent (final int playerLimitDelta, final REASON reason)
  {
    super (reason);

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
    return String.format ("%1$s: Player limit delta: %2$s | %3$s",
            getClass().getSimpleName(), playerLimitDelta, super.toString());
  }

  @RequiredForNetworkSerialization
  private ChangePlayerLimitDeniedEvent()
  {
    playerLimitDelta = 0;
  }
}
