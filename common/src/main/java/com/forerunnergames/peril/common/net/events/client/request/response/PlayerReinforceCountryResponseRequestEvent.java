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

package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public final class PlayerReinforceCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final String countryName;
  private final int reinforcementCount;

  public PlayerReinforceCountryResponseRequestEvent (final String countryName, final int reinforcementCount)
  {

    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (reinforcementCount, "reinforcementCount");

    this.countryName = countryName;
    this.reinforcementCount = reinforcementCount;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerReinforceCountryRequestEvent.class;
  }

  public String getCountryName ()
  {
    return this.countryName;
  }

  public int getReinforcementCount ()
  {
    return this.reinforcementCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: CountryName: {} | ReinforcementCount: {}", getClass ().getSimpleName (),
                           this.countryName, this.reinforcementCount);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountryResponseRequestEvent ()
  {
    countryName = null;
    reinforcementCount = 0;
  }
}
