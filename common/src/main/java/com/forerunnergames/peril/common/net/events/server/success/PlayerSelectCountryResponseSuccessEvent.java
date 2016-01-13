package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.defaults.DefaultPlayerSelectCountryResponseEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryResponseSuccessEvent extends DefaultPlayerSelectCountryResponseEvent
        implements PlayerResponseSuccessEvent
{
  private final PlayerPacket player;

  public PlayerSelectCountryResponseSuccessEvent (final PlayerPacket player, final String selectedCountryName)
  {
    super (selectedCountryName);

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

  public String getPlayerColor ()
  {
    return player.getColor ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}]", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseSuccessEvent ()
  {
    player = null;
  }
}
