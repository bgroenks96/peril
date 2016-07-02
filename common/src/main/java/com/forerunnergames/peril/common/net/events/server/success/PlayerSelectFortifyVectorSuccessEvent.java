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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectFortifyVectorSuccessEvent extends AbstractPlayerEvent implements PlayerSuccessEvent
{
  private final CountryPacket sourceCountry;
  private final CountryPacket destCountry;

  public PlayerSelectFortifyVectorSuccessEvent (final PlayerPacket player,
                                                final CountryPacket sourceCountry,
                                                final CountryPacket destCountry)
  {
    super (player);

    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (destCountry, "destCountry");

    this.sourceCountry = sourceCountry;
    this.destCountry = destCountry;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public CountryPacket getDestinationCountry ()
  {
    return destCountry;
  }

  public String getSourceCountryName ()
  {
    return sourceCountry.getName ();
  }

  public String getDestinationCountryName ()
  {
    return destCountry.getName ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: {} | DestCountry: {}", super.toString (), sourceCountry, destCountry);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectFortifyVectorSuccessEvent ()
  {
    sourceCountry = null;
    destCountry = null;
  }
}
