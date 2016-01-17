package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerClaimCountryResponseDeniedEvent extends AbstractDeniedEvent <Reason>
        implements PlayerDeniedEvent <Reason>
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

  public enum Reason
  {
    COUNTRY_ALREADY_CLAIMED,
    COUNTRY_DOES_NOT_EXIST,
    COUNTRY_DISABLED,
    PLAYER_ARMY_COUNT_UNDERFLOW;
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
