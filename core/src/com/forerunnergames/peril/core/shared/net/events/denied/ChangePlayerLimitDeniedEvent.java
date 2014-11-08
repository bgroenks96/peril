package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class ChangePlayerLimitDeniedEvent implements DeniedEvent
{
  private final int playerLimitDelta;
  private final DeniedEvent deniedEvent;

  public ChangePlayerLimitDeniedEvent (final int playerLimitDelta, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    this.playerLimitDelta = playerLimitDelta;
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
  }

  public int getPlayerLimitDelta()
  {
    return playerLimitDelta;
  }

  @Override
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Player limit delta: %2$s | %3$s",
            getClass().getSimpleName(), playerLimitDelta, deniedEvent);
  }

  // Required for network serialization
  private ChangePlayerLimitDeniedEvent()
  {
    playerLimitDelta = 0;
    deniedEvent = null;
  }
}
