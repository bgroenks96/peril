/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.playmap.continent;

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultContinentArmyModel implements ContinentArmyModel
{
  private final ContinentGraphModel continentGraphModel;

  public DefaultContinentArmyModel (final ContinentGraphModel continentGraphModel)
  {
    Arguments.checkIsNotNull (continentGraphModel, "continentGraphModel");

    this.continentGraphModel = continentGraphModel;
  }

  @Override
  public int getArmyCountFor (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");
    Preconditions.checkIsTrue (continentGraphModel.existsContinentWith (continentId),
                               Strings.format ("No continent with id [{}] exists.", continentId));

    int totalArmyCount = 0;
    for (final CountryPacket country : continentGraphModel.getCountriesIn (continentId))
    {
      totalArmyCount += country.getArmyCount ();
    }
    return totalArmyCount;
  }

  @Override
  public boolean armyCountIsAtLeast (final int minArmyCount, final Id continentId)
  {
    Arguments.checkIsNotNegative (minArmyCount, "minArmyCount");
    Arguments.checkIsNotNull (continentId, "continentId");

    return getArmyCountFor (continentId) >= minArmyCount;
  }
}