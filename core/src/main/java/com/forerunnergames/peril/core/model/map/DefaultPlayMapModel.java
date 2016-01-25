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

package com.forerunnergames.peril.core.model.map;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.map.continent.ContinentArmyModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.tools.common.Arguments;

public class DefaultPlayMapModel implements PlayMapModel
{
  private final CountryOwnerModel countryOwnerModel;
  private final CountryMapGraphModel countryMapGraphModel;
  private final CountryArmyModel countryArmyModel;
  private final ContinentOwnerModel continentOwnerModel;
  private final ContinentMapGraphModel continentMapGraphModel;
  private final ContinentArmyModel continentArmyModel;
  private final GameRules gameRules;

  public DefaultPlayMapModel (final CountryMapGraphModel countryMapGraphModel,
                              final CountryOwnerModel countryOwnerModel,
                              final CountryArmyModel countryArmyModel,
                              final ContinentMapGraphModel continentMapGraphModel,
                              final ContinentOwnerModel continentOwnerModel,
                              final ContinentArmyModel continentArmyModel,
                              final GameRules gameRules)
  {
    Arguments.checkIsNotNull (countryMapGraphModel, "countryMapGraphModel");
    Arguments.checkIsNotNull (countryOwnerModel, "countryOwnerModel");
    Arguments.checkHasNoNullElements (countryMapGraphModel, "countryMapGraphModel");
    Arguments.checkIsNotNull (countryArmyModel, "countryArmyModel");
    Arguments.checkIsNotNull (continentMapGraphModel, "continentMapGraphModel");
    Arguments.checkIsNotNull (continentOwnerModel, "continentOwnerModel");
    Arguments.checkHasNoNullElements (continentMapGraphModel, "continentMapGraphModel");
    Arguments.checkIsNotNull (continentArmyModel, "continentArmyModel");

    this.countryMapGraphModel = countryMapGraphModel;
    this.countryOwnerModel = countryOwnerModel;
    this.countryArmyModel = countryArmyModel;
    this.continentMapGraphModel = continentMapGraphModel;
    this.continentOwnerModel = continentOwnerModel;
    this.continentArmyModel = continentArmyModel;
    this.gameRules = gameRules;
  }

  @Override
  public CountryOwnerModel getCountryOwnerModel ()
  {
    return countryOwnerModel;
  }

  @Override
  public CountryMapGraphModel getCountryMapGraphModel ()
  {
    return countryMapGraphModel;
  }

  @Override
  public CountryArmyModel getCountryArmyModel ()
  {
    return countryArmyModel;
  }

  @Override
  public ContinentOwnerModel getContinentOwnerModel ()
  {
    return continentOwnerModel;
  }

  @Override
  public ContinentMapGraphModel getContinentMapGraphModel ()
  {
    return continentMapGraphModel;
  }

  @Override
  public ContinentArmyModel getContinentArmyModel ()
  {
    return continentArmyModel;
  }

  @Override
  public GameRules getRules ()
  {
    return gameRules;
  }

  @Override
  public int getCountryCount ()
  {
    return countryMapGraphModel.size ();
  }

  @Override
  public int getContinentCount ()
  {
    return continentMapGraphModel.size ();
  }
}
