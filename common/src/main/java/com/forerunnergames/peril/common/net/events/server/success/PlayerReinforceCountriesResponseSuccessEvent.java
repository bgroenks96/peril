package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseSuccessEvent;

public class PlayerReinforceCountriesResponseSuccessEvent implements ResponseSuccessEvent
{
  private final PlayerPacket player;

  public PlayerReinforceCountriesResponseSuccessEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesResponseSuccessEvent ()
  {
    player = null;
  }
}
