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

package com.forerunnergames.peril.core.model.playmap;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentArmyModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryOwnerModel;

public interface PlayMapModel
{
  CountryOwnerModel getCountryOwnerModel ();

  CountryGraphModel getCountryGraphModel ();

  CountryArmyModel getCountryArmyModel ();

  ContinentOwnerModel getContinentOwnerModel ();

  ContinentGraphModel getContinentGraphModel ();

  ContinentArmyModel getContinentArmyModel ();

  GameRules getRules ();

  int getCountryCount ();

  int getContinentCount ();
}
