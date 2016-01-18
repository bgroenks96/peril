package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerClaimCountryResponseDeniedEvent extends AbstractCountryStateChangeDeniedEvent
{
  private final PlayerPacket player;
  private final String claimedCountryName;

  public PlayerClaimCountryResponseDeniedEvent (final PlayerPacket player,
                                                final String claimedCountryName,
                                                final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (claimedCountryName, "claimedCountryName");

    this.player = player;
    this.claimedCountryName = claimedCountryName;
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

  public String getCountryName ()
  {
    return claimedCountryName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Player: [{}] | Claimed Country Name: {}", super.toString (), player,
                           claimedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryResponseDeniedEvent ()
  {
    player = null;
    claimedCountryName = null;
  }
}
