package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent.Reason;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class SpectatorJoinGameDeniedEvent extends AbstractDeniedEvent <Reason>
{
  private final String deniedName;

  public enum Reason
  {
    GAME_IS_FULL,
    OBSERVING_DISABLED,
    INVALID_NAME,
    DUPLICATE_NAME,
    DUPLICATE_SELF_IDENTITY;
  }

  public SpectatorJoinGameDeniedEvent (final String deniedName, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (deniedName, "deniedName");

    this.deniedName = deniedName;
  }

  public String getSpectatorName ()
  {
    return deniedName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Reason: {}", getClass ().getSimpleName (), deniedName);
  }

  @RequiredForNetworkSerialization
  private SpectatorJoinGameDeniedEvent ()
  {
    deniedName = null;
  }
}
