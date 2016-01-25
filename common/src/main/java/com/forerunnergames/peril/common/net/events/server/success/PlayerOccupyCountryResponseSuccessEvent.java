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
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class PlayerOccupyCountryResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent, CountryOwnerChangedEvent
{
  private final PlayerPacket prevDestCountryOwner;
  private final CountryPacket sourceCountry;
  private final CountryPacket destinationCountry;
  private final int deltaArmyCount;

  public PlayerOccupyCountryResponseSuccessEvent (final PlayerPacket player,
                                                  final PlayerPacket prevDestCountryOwner,
                                                  final CountryPacket sourceCountry,
                                                  final CountryPacket destinationCountry,
                                                  final int deltaArmyCount)
  {
    super (player);

    Arguments.checkIsNotNull (prevDestCountryOwner, "prevDestCountryOwner");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (destinationCountry, "destinationCountry");
    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.prevDestCountryOwner = prevDestCountryOwner;
    this.sourceCountry = sourceCountry;
    this.destinationCountry = destinationCountry;
    this.deltaArmyCount = deltaArmyCount;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public CountryPacket getDestinationCountry ()
  {
    return destinationCountry;
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  /**
   * @return the country whose ownership changed; i.e. the destination country
   */
  @Override
  public CountryPacket getCountry ()
  {
    return destinationCountry;
  }

  /**
   * @return name of the country whose ownership changed; i.e. the destination country
   */
  @Override
  public String getCountryName ()
  {
    return destinationCountry.getName ();
  }

  @Override
  public Optional <PlayerPacket> getPreviousOwner ()
  {
    return Optional.of (prevDestCountryOwner);
  }

  /**
   * @return the new destination country owner; same as {@link #getPlayer()}
   */
  @Override
  public PlayerPacket getNewOwner ()
  {
    return getPlayer ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: [{}] | DestinationCountry: [{}] | DeltaArmyCount: [{}]",
                           super.toString (), sourceCountry, destinationCountry, deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryResponseSuccessEvent ()
  {
    prevDestCountryOwner = null;
    sourceCountry = null;
    destinationCountry = null;
    deltaArmyCount = 0;
  }
}
