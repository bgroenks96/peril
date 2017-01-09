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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerSourceTargetCountryEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerOccupyCountryResponseSuccessEvent extends AbstractPlayerSourceTargetCountryEvent
        implements PlayerResponseSuccessEvent, CountryOwnerChangedEvent
{
  private final PlayerPacket previousTargetCountryOwner;
  private final int deltaArmyCount;

  public PlayerOccupyCountryResponseSuccessEvent (final PlayerPacket player,
                                                  final PlayerPacket previousTargetCountryOwner,
                                                  final CountryPacket sourceCountry,
                                                  final CountryPacket targetCountry,
                                                  final int deltaArmyCount)
  {
    super (player, sourceCountry, targetCountry);

    Arguments.checkIsNotNull (previousTargetCountryOwner, "previousTargetCountryOwner");
    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.previousTargetCountryOwner = previousTargetCountryOwner;
    this.deltaArmyCount = deltaArmyCount;
  }

  /**
   * @return the country whose ownership changed; i.e. the target country
   */
  @Override
  public CountryPacket getCountry ()
  {
    return getTargetCountry ();
  }

  @Override
  public int getCountryArmyCount ()
  {
    return getTargetCountryArmyCount ();
  }

  /**
   * @return name of the country whose ownership changed; i.e. the target country
   */
  @Override
  public String getCountryName ()
  {
    return getTargetCountryName ();
  }

  @Override
  public boolean hasPreviousOwner ()
  {
    return true;
  }

  @Override
  public PlayerPacket getPreviousOwner ()
  {
    return previousTargetCountryOwner;
  }

  @Override
  public String getPreviousOwnerName ()
  {
    return previousTargetCountryOwner.getName ();
  }

  @Override
  public PlayerColor getPreviousOwnerColor ()
  {
    return previousTargetCountryOwner.getColor ();
  }

  @Override
  public boolean hasNewOwner ()
  {
    return true;
  }

  /**
   * @return the new target country owner; same as {@link #getPerson()}
   */
  @Override
  public PlayerPacket getNewOwner ()
  {
    return getPerson ();
  }

  @Override
  public PlayerColor getNewOwnerColor ()
  {
    return getPlayerColor ();
  }

  @Override
  public String getNewOwnerName ()
  {
    return getPersonName ();
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | PreviousTargetCountryOwner: [{}] | DeltaArmyCount: [{}]", super.toString (),
                           previousTargetCountryOwner, deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryResponseSuccessEvent ()
  {
    previousTargetCountryOwner = null;
    deltaArmyCount = 0;
  }
}
