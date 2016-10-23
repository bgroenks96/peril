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

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.playmap.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryOwnerModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public final class PlayMapStateBuilder
{
  private final PlayMapModel playMapModel;

  public PlayMapStateBuilder (final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    this.playMapModel = playMapModel;
  }

  public OngoingCountryConfiguration forCountries (final ImmutableSet <Id> countryIds)
  {
    return new OngoingCountryConfiguration (countryIds);
  }

  public OngoingCountryConfiguration forCountries (final Iterable <Id> countryIds)
  {
    return forCountries (ImmutableSet.copyOf (countryIds));
  }

  public OngoingCountryConfiguration forCountry (final Id countryId)
  {
    return forCountries (ImmutableSet.of (countryId));
  }

  public class OngoingCountryConfiguration
  {
    private final ImmutableSet <Id> countryIds;

    public OngoingCountryConfiguration setOwner (final Id ownerId)
    {
      final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();
      for (final Id country : countryIds)
      {
        final CountryPacket countryPacket = playMapModel.getCountryGraphModel ().countryPacketWith (country);
        final MutatorResult <?> result = countryOwnerModel.requestToAssignCountryOwner (country, ownerId);
        if (result.failed ())
        {
          Exceptions.throwIllegalState ("Failed to assign country [{}] to owner [{}]: {}", countryPacket, ownerId,
                                        result.getFailureReason ());
        }
        result.commitIfSuccessful ();
      }
      return this;
    }

    public OngoingCountryConfiguration addArmies (final int armyCount)
    {
      final CountryArmyModel countryArmyModel = playMapModel.getCountryArmyModel ();
      for (final Id country : countryIds)
      {
        final CountryPacket countryPacket = playMapModel.getCountryGraphModel ().countryPacketWith (country);
        final MutatorResult <?> result = countryArmyModel.requestToAddArmiesToCountry (country, armyCount);
        if (result.failed ())
        {
          Exceptions.throwIllegalState ("Failed to add {} armies to country [{}]: {}", armyCount, countryPacket,
                                        result.getFailureReason ());
        }
        result.commitIfSuccessful ();
      }
      return this;
    }

    private OngoingCountryConfiguration (final ImmutableSet <Id> countryIds)
    {
      assert countryIds != null;

      this.countryIds = countryIds;
    }
  }
}
