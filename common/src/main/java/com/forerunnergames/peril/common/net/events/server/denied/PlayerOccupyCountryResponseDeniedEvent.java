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

import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import javax.annotation.Nullable;

public final class PlayerOccupyCountryResponseDeniedEvent
        extends AbstractPlayerChangeCountryDeniedEvent <PlayerOccupyCountryResponseRequestEvent> implements
        PlayerResponseDeniedEvent <PlayerOccupyCountryResponseRequestEvent, AbstractPlayerChangeCountryDeniedEvent.Reason>
{
  @Nullable
  private final PlayerOccupyCountryRequestEvent originalRequest;

  public PlayerOccupyCountryResponseDeniedEvent (final PlayerPacket player,
                                                 // FIXME This should not be necessary.
                                                 @Nullable final PlayerOccupyCountryRequestEvent originalRequest,
                                                 final PlayerOccupyCountryResponseRequestEvent deniedRequest,
                                                 final Reason reason)
  {
    super (player, deniedRequest, reason);

    Arguments.checkIsNotNull (originalRequest, "originalRequest");

    this.originalRequest = originalRequest;
  }

  public boolean hasOriginalRequest ()
  {
    return originalRequest != null;
  }

  @Nullable
  public PlayerOccupyCountryRequestEvent getOriginalRequest ()
  {
    return originalRequest;
  }

  public String getSourceCountryName ()
  {
    return originalRequest != null ? originalRequest.getSourceCountryName () : "";
  }

  public String getTargetCountryName ()
  {
    return originalRequest != null ? originalRequest.getTargetCountryName () : "";
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | OriginalRequest: [{}]", super.toString (), originalRequest);
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryResponseDeniedEvent ()
  {
    originalRequest = null;
  }
}
