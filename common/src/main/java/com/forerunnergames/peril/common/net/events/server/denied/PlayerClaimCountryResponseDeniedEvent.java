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

import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerClaimCountryResponseDeniedEvent
        extends AbstractPlayerChangeCountryDeniedEvent <PlayerClaimCountryResponseRequestEvent> implements
        PlayerResponseDeniedEvent <PlayerClaimCountryResponseRequestEvent, AbstractPlayerChangeCountryDeniedEvent.Reason>

{
  private final String claimedCountryName;

  public PlayerClaimCountryResponseDeniedEvent (final PlayerPacket player,
                                                final String claimedCountryName,
                                                final PlayerClaimCountryResponseRequestEvent deniedRequest,
                                                final Reason reason)
  {
    super (player, deniedRequest, reason);

    Arguments.checkIsNotNull (claimedCountryName, "claimedCountryName");

    this.claimedCountryName = claimedCountryName;
  }

  public String getCountryName ()
  {
    return claimedCountryName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ClaimedCountryName: {}", super.toString (), claimedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryResponseDeniedEvent ()
  {
    claimedCountryName = null;
  }
}
