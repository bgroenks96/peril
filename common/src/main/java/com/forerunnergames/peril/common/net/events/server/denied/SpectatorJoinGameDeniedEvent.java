package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent.Reason;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class SpectatorJoinGameDeniedEvent extends AbstractDeniedEvent <Reason>
{
  private final String spectatorName;

  public enum Reason
  {
    GAME_IS_FULL,
    SPECTATING_DISABLED,
    INVALID_NAME,
    DUPLICATE_NAME;
  }

  public SpectatorJoinGameDeniedEvent (final String deniedName, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (deniedName, "deniedName");

    this.spectatorName = deniedName;
  }

  public String getSpectatorName ()
  {
    return spectatorName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SpectatorName: {}", super.toString (), spectatorName);
  }

  @RequiredForNetworkSerialization
  private SpectatorJoinGameDeniedEvent ()
  {
    spectatorName = null;
  }
}
