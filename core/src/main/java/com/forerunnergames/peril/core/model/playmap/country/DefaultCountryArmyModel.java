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

package com.forerunnergames.peril.core.model.playmap.country;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent.Reason;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.MutatorResult.MutatorCallback;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.math.IntMath;

public final class DefaultCountryArmyModel implements CountryArmyModel
{
  private final CountryGraphModel countryGraphModel;
  private final GameRules rules;

  public DefaultCountryArmyModel (final CountryGraphModel countryGraphModel, final GameRules rules)
  {
    Arguments.checkIsNotNull (countryGraphModel, "countryGraphModel");
    Arguments.checkIsNotNull (rules, "rules");

    this.countryGraphModel = countryGraphModel;
    this.rules = rules;
  }

  @Override
  public MutatorResult <Reason> requestToAddArmiesToCountry (final Id countryId, final int armyCount)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    final Country country = countryGraphModel.modelCountryWith (countryId);
    if (IntMath.checkedAdd (country.getArmyCount (), armyCount) > rules.getMaxArmiesOnCountry ())
    {
      return MutatorResult.failure (Reason.COUNTRY_ARMY_COUNT_OVERFLOW);
    }

    return MutatorResult.success (new MutatorCallback ()
    {
      @Override
      public void commitChanges ()
      {
        country.addArmies (armyCount);
      }
    });
  }

  @Override
  public MutatorResult <Reason> requestToRemoveArmiesFromCountry (final Id countryId, final int armyCount)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    final Country country = countryGraphModel.modelCountryWith (countryId);
    if (IntMath.checkedSubtract (country.getArmyCount (), armyCount) < rules.getMinArmiesOnCountry ())
    {
      return MutatorResult.failure (Reason.COUNTRY_ARMY_COUNT_UNDERFLOW);
    }

    return MutatorResult.success (new MutatorCallback ()
    {
      @Override
      public void commitChanges ()
      {
        country.removeArmies (armyCount);
      }
    });
  }

  @Override
  public int getArmyCountFor (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Preconditions.checkIsTrue (countryGraphModel.existsCountryWith (countryId),
                               Strings.format ("No country with id [{}] exists.", countryId));

    return countryGraphModel.modelCountryWith (countryId).getArmyCount ();
  }

  @Override
  public boolean armyCountIs (final int armyCount, final Id countryId)
  {
    Arguments.checkIsNotNegative (armyCount, "armyCount");
    Arguments.checkIsNotNull (countryId, "countryId");

    return getArmyCountFor (countryId) == armyCount;
  }

  @Override
  public boolean armyCountIsAtLeast (final int armyCount, final Id countryId)
  {
    Arguments.checkIsNotNegative (armyCount, "armyCount");
    Arguments.checkIsNotNull (countryId, "countryId");

    return getArmyCountFor (countryId) >= armyCount;
  }

  @Override
  public void resetAllCountries ()
  {
    for (final Id countryId : countryGraphModel)
    {
      final Country country = countryGraphModel.modelCountryWith (countryId);
      country.removeArmies (country.getArmyCount ());
    }
  }
}
