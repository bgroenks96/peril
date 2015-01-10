package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerLimitEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

public final class ChangePlayerLimitSuccessEvent implements SuccessEvent, PlayerLimitEvent
{
  private final int newPlayerLimit;
  private final int oldPlayerLimit;
  private final int playerLimitDelta;

  public ChangePlayerLimitSuccessEvent (final int newPlayerLimit, final int oldPlayerLimit, final int playerLimitDelta)
  {
    Arguments.checkIsNotNegative (newPlayerLimit, "newPlayerLimit");
    Arguments.checkIsNotNegative (oldPlayerLimit, "oldPlayerLimit");

    this.newPlayerLimit = newPlayerLimit;
    this.oldPlayerLimit = oldPlayerLimit;
    this.playerLimitDelta = playerLimitDelta;
  }

  @Override
  public final int getPlayerLimitDelta()
  {
    return playerLimitDelta;
  }

  public final int getNewPlayerLimit()
  {
    return newPlayerLimit;
  }

  public final int getOldPlayerLimit()
  {
    return oldPlayerLimit;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: New Player Limit: %2$s | Old Player Limit: %3$s | Player limit delta: %4$s",
            getClass().getSimpleName(), newPlayerLimit, oldPlayerLimit, playerLimitDelta);
  }

  @RequiredForNetworkSerialization
  private ChangePlayerLimitSuccessEvent()
  {
    newPlayerLimit = 0;
    oldPlayerLimit = 0;
    playerLimitDelta = 0;
  }
}
