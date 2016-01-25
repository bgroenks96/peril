/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
