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

package com.forerunnergames.peril.core.model.playmap;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentArmyModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryOwnerModel;
import com.forerunnergames.tools.common.Arguments;

public class DefaultPlayMapModel implements PlayMapModel
{
  private final CountryOwnerModel countryOwnerModel;
  private final CountryGraphModel countryGraphModel;
  private final CountryArmyModel countryArmyModel;
  private final ContinentOwnerModel continentOwnerModel;
  private final ContinentGraphModel continentGraphModel;
  private final ContinentArmyModel continentArmyModel;
  private final GameRules gameRules;

  public DefaultPlayMapModel (final CountryGraphModel countryGraphModel,
                              final CountryOwnerModel countryOwnerModel,
                              final CountryArmyModel countryArmyModel,
                              final ContinentGraphModel continentGraphModel,
                              final ContinentOwnerModel continentOwnerModel,
                              final ContinentArmyModel continentArmyModel,
                              final GameRules gameRules)
  {
    Arguments.checkIsNotNull (countryGraphModel, "countryGraphModel");
    Arguments.checkIsNotNull (countryOwnerModel, "countryOwnerModel");
    Arguments.checkHasNoNullElements (countryGraphModel, "countryGraphModel");
    Arguments.checkIsNotNull (countryArmyModel, "countryArmyModel");
    Arguments.checkIsNotNull (continentGraphModel, "continentGraphModel");
    Arguments.checkIsNotNull (continentOwnerModel, "continentOwnerModel");
    Arguments.checkHasNoNullElements (continentGraphModel, "continentGraphModel");
    Arguments.checkIsNotNull (continentArmyModel, "continentArmyModel");

    this.countryGraphModel = countryGraphModel;
    this.countryOwnerModel = countryOwnerModel;
    this.countryArmyModel = countryArmyModel;
    this.continentGraphModel = continentGraphModel;
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
  public CountryGraphModel getCountryGraphModel ()
  {
    return countryGraphModel;
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
  public ContinentGraphModel getContinentGraphModel ()
  {
    return continentGraphModel;
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
    return countryGraphModel.size ();
  }

  @Override
  public int getContinentCount ()
  {
    return continentGraphModel.size ();
  }
}
