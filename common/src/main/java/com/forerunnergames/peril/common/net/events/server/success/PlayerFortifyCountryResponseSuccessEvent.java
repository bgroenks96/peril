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
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class PlayerFortifyCountryResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent
{
  private final Optional <CountryPacket> sourceCountry;
  private final Optional <CountryPacket> targetCountry;
  private final int fortifyArmyCount;

  public PlayerFortifyCountryResponseSuccessEvent (final PlayerPacket player,
                                                   final CountryPacket sourceCountry,
                                                   final CountryPacket targetCountry,
                                                   final int fortifyArmyCount)
  {
    super (player);

    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkIsNotNegative (fortifyArmyCount, "fortifyArmyCount");

    this.sourceCountry = Optional.of (sourceCountry);
    this.targetCountry = Optional.of (targetCountry);
    this.fortifyArmyCount = fortifyArmyCount;
  }

  public PlayerFortifyCountryResponseSuccessEvent (final PlayerPacket player)
  {
    super (player);

    Arguments.checkIsNotNull (player, "player");

    sourceCountry = Optional.absent ();
    targetCountry = Optional.absent ();
    fortifyArmyCount = 0;
  }

  public boolean isCountryDataPresent ()
  {
    return sourceCountry.isPresent () && targetCountry.isPresent ();
  }

  public Optional <CountryPacket> getSourceCountry ()
  {
    return sourceCountry;
  }

  public Optional <CountryPacket> getTargetCountry ()
  {
    return targetCountry;
  }

  public int getFortifyArmyCount ()
  {
    return fortifyArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: SourceCountry: {} | TargetCountry: {} | FortifyArmyCount: {}", sourceCountry,
                           targetCountry, fortifyArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryResponseSuccessEvent ()
  {
    sourceCountry = null;
    targetCountry = null;
    fortifyArmyCount = 0;
  }
}
