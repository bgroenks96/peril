package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerReinforceCountriesResponseDeniedEvent extends AbstractCountryStateChangeDeniedEvent
        implements PlayerResponseDeniedEvent <Reason>
{
  private final PlayerPacket player;

  public PlayerReinforceCountriesResponseDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public String getPlayerColor ()
  {
    return player.getColor ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Player: [{}]", super.toString (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesResponseDeniedEvent ()
  {
    player = null;
  }
}
