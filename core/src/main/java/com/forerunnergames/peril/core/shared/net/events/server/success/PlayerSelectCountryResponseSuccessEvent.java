package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
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
