/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.client.request.inform;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerReinforceCountryEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerReinforceCountryRequestEvent implements PlayerInformRequestEvent <PlayerReinforceCountryEvent>
{
  private final String countryName;
  private final int reinforcementCount;

  public PlayerReinforceCountryRequestEvent (final String countryName, final int reinforcementCount)
  {

    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (reinforcementCount, "reinforcementCount");

    this.countryName = countryName;
    this.reinforcementCount = reinforcementCount;
  }

  @Override
  public Class <PlayerReinforceCountryEvent> getQuestionType ()
  {
    return PlayerReinforceCountryEvent.class;
  }

  public String getCountryName ()
  {
    return countryName;
  }

  public int getReinforcementCount ()
  {
    return reinforcementCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: CountryName: {} | ReinforcementCount: {}", getClass ().getSimpleName (), countryName,
                           reinforcementCount);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountryRequestEvent ()
  {
    countryName = null;
    reinforcementCount = 0;
  }
}
