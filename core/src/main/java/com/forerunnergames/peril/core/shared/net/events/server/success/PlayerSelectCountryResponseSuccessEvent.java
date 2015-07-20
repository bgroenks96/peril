package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryResponseEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseSuccessEvent;

public final class PlayerSelectCountryResponseSuccessEvent extends DefaultPlayerSelectCountryResponseEvent implements
        ResponseSuccessEvent
{
  private final PlayerPacket player;

  public PlayerSelectCountryResponseSuccessEvent (final String selectedCountryName, final PlayerPacket player)
  {
    super (selectedCountryName);

    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public String getPlayerColor ()
  {
    return player.getColor ();
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseSuccessEvent ()
  {
    super (null);

    player = null;
  }
}
