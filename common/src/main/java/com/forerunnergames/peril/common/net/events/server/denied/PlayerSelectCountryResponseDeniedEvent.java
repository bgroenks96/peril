package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.defaults.DefaultPlayerSelectCountryResponseEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryResponseDeniedEvent extends DefaultPlayerSelectCountryResponseEvent
        implements PlayerResponseDeniedEvent <PlayerSelectCountryResponseDeniedEvent.Reason>
{
  private final PlayerPacket player;
  private final Reason reason;

  public PlayerSelectCountryResponseDeniedEvent (final PlayerPacket player,
                                                 final String selectedCountryName,
                                                 final Reason reason)
  {
    super (selectedCountryName);

    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (reason, "reason");

    this.player = player;
    this.reason = reason;
  }

  public enum Reason
  {
    COUNTRY_DOES_NOT_EXIST,
    COUNTRY_DISABLED,
    COUNTRY_ALREADY_OWNED;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public Reason getReason ()
  {
    return reason;
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseDeniedEvent ()
  {
    super (null);
    player = null;
    reason = null;
  }
}
