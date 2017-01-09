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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerCountryArmiesChangedEvent extends AbstractPlayerEvent
        implements PlayerArmiesChangedEvent, CountryArmiesChangedEvent
{
  private final CountryPacket country;
  private final int playerDeltaArmyCount;
  private final int countryDeltaArmyCount;

  protected AbstractPlayerCountryArmiesChangedEvent (final PlayerPacket player,
                                                     final CountryPacket country,
                                                     @AllowNegative final int playerDeltaArmyCount,
                                                     @AllowNegative final int countryDeltaArmyCount)
  {
    super (player);

    Arguments.checkIsNotNull (country, "country");

    this.country = country;
    this.playerDeltaArmyCount = playerDeltaArmyCount;
    this.countryDeltaArmyCount = countryDeltaArmyCount;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerCountryArmiesChangedEvent ()
  {
    country = null;
    playerDeltaArmyCount = 0;
    countryDeltaArmyCount = 0;
  }

  @Override
  public CountryPacket getCountry ()
  {
    return country;
  }

  @Override
  public int getCountryArmyCount ()
  {
    return country.getArmyCount ();
  }

  @Override
  public String getCountryName ()
  {
    return country.getName ();
  }

  @Override
  public int getCountryDeltaArmyCount ()
  {
    return countryDeltaArmyCount;
  }

  @Override
  public int getPlayerDeltaArmyCount ()
  {
    return playerDeltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Country: [{}] | PlayerDeltaArmyCount: [{}] | CountryDeltaArmyCount: [{}]",
                           super.toString (), country, playerDeltaArmyCount, countryDeltaArmyCount);
  }
}
