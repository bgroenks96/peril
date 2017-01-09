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

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerSourceTargetCountryEvent extends AbstractPlayerEvent
{
  private final CountryPacket sourceCountry;
  private final CountryPacket targetCountry;

  public AbstractPlayerSourceTargetCountryEvent (final PlayerPacket player,
                                                 final CountryPacket sourceCountry,
                                                 final CountryPacket targetCountry)
  {
    super (player);

    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");

    this.sourceCountry = sourceCountry;
    this.targetCountry = targetCountry;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerSourceTargetCountryEvent ()
  {
    sourceCountry = null;
    targetCountry = null;
  }

  public final CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public final String getSourceCountryName ()
  {
    return sourceCountry.getName ();
  }

  public final int getSourceCountryArmyCount ()
  {
    return sourceCountry.getArmyCount ();
  }

  public final CountryPacket getTargetCountry ()
  {
    return targetCountry;
  }

  public final String getTargetCountryName ()
  {
    return targetCountry.getName ();
  }

  public final int getTargetCountryArmyCount ()
  {
    return targetCountry.getArmyCount ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: [{}] | TargetCountry: [{}]", super.toString (), sourceCountry,
                           targetCountry);
  }
}
