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

public interface PlayMapModel
{
  CountryOwnerModel getCountryOwnerModel ();

  CountryMapGraphModel getCountryMapGraphModel ();

  CountryArmyModel getCountryArmyModel ();

  ContinentOwnerModel getContinentOwnerModel ();

  ContinentMapGraphModel getContinentMapGraphModel ();

  ContinentArmyModel getContinentArmyModel ();

  GameRules getRules ();

  int getCountryCount ();

  int getContinentCount ();
}
