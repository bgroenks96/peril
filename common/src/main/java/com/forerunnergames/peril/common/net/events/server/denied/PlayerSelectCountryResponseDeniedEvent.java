package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.defaults.DefaultPlayerSelectCountryResponseEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryResponseDeniedEvent extends DefaultPlayerSelectCountryResponseEvent
        implements PlayerResponseDeniedEvent <PlayerSelectCountryResponseDeniedEvent.Reason>
{
  public enum Reason
  {
    COUNTRY_ALREADY_OWNED,
    COUNTRY_DOES_NOT_EXIST,
    COUNTRY_DISABLED,
    NOT_OWNER_OF_COUNTRY,
    INSUFFICIENT_ARMIES,
    COUNTRY_NOT_ADJACENT
  }

  private final PlayerPacket player;
  private final Reason reason;

  public PlayerSelectCountryResponseDeniedEvent (final PlayerPacket player,
                                                 final String selectedCountryName,
                                                 final PlayerSelectCountryResponseDeniedEvent.Reason reason)
  {
    super (selectedCountryName);

    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (reason, "reason");

    this.player = player;
    this.reason = reason;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return getPlayer ().getName ();
  }

  @Override
  public PlayerSelectCountryResponseDeniedEvent.Reason getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Player: [{}] | Reason: {}", super.toString (), player, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseDeniedEvent ()
  {
    player = null;
    reason = null;
  }
}
