package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryResponseDeniedEvent extends AbstractDeniedEvent <Reason>
        implements PlayerDeniedEvent <Reason>
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
  private final String selectedCountryName;

  public PlayerSelectCountryResponseDeniedEvent (final PlayerPacket player,
                                                 final String selectedCountryName,
                                                 final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (selectedCountryName, "selectedCountryName");

    this.player = player;
    this.selectedCountryName = selectedCountryName;
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
  public String toString ()
  {
    return Strings.format ("{} | Player: [{}] | SelectedCountryName: {}", super.toString (), player,
                           selectedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseDeniedEvent ()
  {
    player = null;
    selectedCountryName = null;
  }
}
