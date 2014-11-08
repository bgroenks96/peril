package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class PlayerJoinGameDeniedEvent implements DeniedEvent
{
  private final String playerName;
  private final DeniedEvent deniedEvent;

  public PlayerJoinGameDeniedEvent (final String playerName, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    this.playerName = playerName;
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
  }

  @Override
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  public String getPlayerName()
  {
    return playerName;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Player name: %2$s | %3$s",
            getClass().getSimpleName(), playerName, deniedEvent.toString());
  }

  // Required for network serialization
  private PlayerJoinGameDeniedEvent()
  {
    playerName = null;
    deniedEvent = null;
  }
}
