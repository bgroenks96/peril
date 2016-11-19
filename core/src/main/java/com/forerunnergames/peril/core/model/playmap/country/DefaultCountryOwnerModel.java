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
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.MutatorResult.MutatorCallback;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;

public final class DefaultCountryOwnerModel implements CountryOwnerModel
{
  private final CountryGraphModel countryGraphModel;
  private final Map <Id, Id> countryIdsToOwnerIds = new HashMap <> ();

  public DefaultCountryOwnerModel (final CountryGraphModel countryGraphModel, final GameRules rules)
  {
    Arguments.checkIsNotNull (countryGraphModel, "countryGraphModel");
    Arguments.checkIsNotNull (rules, "rules");
    Preconditions.checkIsTrue (countryGraphModel.size () >= rules.getMinTotalCountryCount (), "Country count of "
            + countryGraphModel.size () + " is below minimum of " + rules.getMinTotalCountryCount () + "!");
    Preconditions.checkIsTrue (countryGraphModel.size () <= rules.getMaxTotalCountryCount (), "Country count "
            + countryGraphModel.size () + " is above maximum of " + rules.getMaxTotalCountryCount () + "!");

    this.countryGraphModel = countryGraphModel;
  }

  @Override
  public boolean hasAnyUnownedCountries ()
  {
    // if the country -> owner map is less than the size of the country ID map, then there must be
    // some countries without an assigned owner.
    return countryIdsToOwnerIds.size () < countryGraphModel.size ();
  }

  @Override
  public boolean allCountriesAreUnowned ()
  {
    return countryIdsToOwnerIds.isEmpty ();
  }

  @Override
  public boolean hasAnyOwnedCountries ()
  {
    return !countryIdsToOwnerIds.isEmpty ();
  }

  @Override
  public boolean allCountriesAreOwned ()
  {
    return countryIdsToOwnerIds.size () == countryGraphModel.size ();
  }

  @Override
  public boolean isCountryOwned (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Preconditions.checkIsTrue (countryGraphModel.existsCountryWith (countryId),
                               Strings.format ("No country with Id [{}] exists.", countryId));

    return countryIdsToOwnerIds.containsKey (countryId);
  }

  @Override
  public boolean isCountryOwned (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Optional <Country> country = getCountryByName (countryName);
    Preconditions.checkIsTrue (country.isPresent (), "No country with name [{}] exists.", countryName);

    return countryGraphModel.existsCountryWith (countryName) && isCountryOwned (country.get ().getId ());
  }

  @Override
  public boolean isCountryOwnedBy (final Id countryId, final Id playerId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNull (playerId, "playerId");

    return countryGraphModel.existsCountryWith (countryId) && isCountryOwned (countryId)
            && ownerOf (countryId).is (playerId);
  }

  /**
   * Assigns the Country specified by countryId to the owner specified by ownerId.
   *
   * Note: the country MUST be unowned!
   *
   * @return success/failure Result with reason
   */
  @Override
  public MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> requestToAssignCountryOwner (final Id countryId,
                                                                                                    final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");
    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!countryGraphModel.existsCountryWith (countryId)) return MutatorResult.failure (Reason.COUNTRY_DOES_NOT_EXIST);
    if (isCountryOwned (countryId)) return MutatorResult.failure (Reason.COUNTRY_ALREADY_OWNED);
    //@formatter:on

    return MutatorResult.success (new MutatorCallback ()
    {
      @Override
      public void commitChanges ()
      {
        countryIdsToOwnerIds.put (countryId, ownerId);
      }
    });
  }

  /**
   * Assigns the Country specified by countryId to the owner specified by ownerId.
   *
   * @return success/failure Result with reason
   */
  @Override
  public MutatorResult <PlayerOccupyCountryResponseDeniedEvent.Reason> requestToReassignCountryOwner (final Id countryId,
                                                                                                      final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");
    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!countryGraphModel.existsCountryWith (countryId)) return MutatorResult.failure (Reason.COUNTRY_DOES_NOT_EXIST);
    if (isCountryOwnedBy (countryId, ownerId)) return MutatorResult.failure (Reason.COUNTRY_ALREADY_OWNED);
    //@formatter:on

    return MutatorResult.success (new MutatorCallback ()
    {
      @Override
      public void commitChanges ()
      {
        countryIdsToOwnerIds.put (countryId, ownerId);
      }
    });
  }

  /**
   * Unassigns the Country specified by the given id from its current owner.
   *
   * @return success/failure Result
   */
  @Override
  public MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> requestToUnassignCountry (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!countryGraphModel.existsCountryWith (countryId)) return MutatorResult.failure (Reason.COUNTRY_DOES_NOT_EXIST);
    //@formatter:on

    return MutatorResult.success (new MutatorCallback ()
    {
      @Override
      public void commitChanges ()
      {
        countryIdsToOwnerIds.remove (countryId);
      }
    });
  }

  /**
   * @return the Id of the owner to which the Country specified by countryId is currently assigned.
   */
  @Override
  public Id ownerOf (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    checkValidCountryId (countryId);

    if (isCountryOwned (countryId)) return countryIdsToOwnerIds.get (countryId);
    else throw new IllegalStateException (Strings.format ("Country with id [{}] has no owner.", countryId));
  }

  @Override
  public ImmutableSet <CountryPacket> getCountriesOwnedBy (final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");

    if (!countryIdsToOwnerIds.containsValue (ownerId)) return ImmutableSet.of ();

    final ImmutableSet.Builder <CountryPacket> countryBuilder = ImmutableSet.builder ();

    for (final Map.Entry <Id, Id> countryIdToOwnerIdEntry : countryIdsToOwnerIds.entrySet ())
    {
      if (!countryIdToOwnerIdEntry.getValue ().equals (ownerId)) continue;

      final Country country = countryGraphModel.modelCountryWith (countryIdToOwnerIdEntry.getKey ());

      if (country != null) countryBuilder.add (CountryPackets.from (country));
    }

    return countryBuilder.build ();
  }

  @Override
  public ImmutableSet <CountryPacket> getUnownedCountries ()
  {
    final ImmutableSet.Builder <CountryPacket> unownedCountries = ImmutableSet.builder ();
    for (final Id countryId : countryGraphModel)
    {
      if (!isCountryOwned (countryId)) unownedCountries.add (countryGraphModel.countryPacketWith (countryId));
    }
    return unownedCountries.build ();
  }

  @Override
  public ImmutableSet <String> getCountryNamesOwnedBy (final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");

    if (!countryIdsToOwnerIds.containsValue (ownerId)) return ImmutableSet.of ();

    final ImmutableSet.Builder <String> countryNameBuilder = ImmutableSet.builder ();

    for (final Map.Entry <Id, Id> countryIdToOwnerIdEntry : countryIdsToOwnerIds.entrySet ())
    {
      if (!countryIdToOwnerIdEntry.getValue ().equals (ownerId)) continue;

      final Country country = countryGraphModel.modelCountryWith (countryIdToOwnerIdEntry.getKey ());

      if (country != null) countryNameBuilder.add (country.getName ());
    }

    return countryNameBuilder.build ();
  }

  @Override
  public void unassignAllCountries ()
  {
    countryIdsToOwnerIds.clear ();
  }

  @Override
  public void unassignAllCountriesOwnedBy (final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");

    for (final Country country : countryGraphModel.getCountries ())
    {
      if (countryIdsToOwnerIds.containsKey (country.getId ()) && countryIdsToOwnerIds.containsValue (ownerId))
      {
        requestToUnassignCountry (country.getId ());
      }
    }
  }

  @Override
  public int countCountriesOwnedBy (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    return getCountriesOwnedBy (id).size ();
  }

  @Override
  public int getOwnedCountryCount ()
  {
    return getOwnedCountries ().size ();
  }

  @Override
  public boolean ownedCountryCountIs (final int n)
  {
    Arguments.checkIsNotNegative (n, "n");

    return getOwnedCountryCount () == n;
  }

  @Override
  public boolean ownedCountryCountIsAtLeast (final int n)
  {
    Arguments.checkIsNotNegative (n, "n");

    return getOwnedCountryCount () >= n;
  }

  public static ImmutableSet <Country> generateDefaultCountries (final GameRules gameRules)
  {
    Arguments.checkIsNotNull (gameRules, "gameRules");

    final int count = gameRules.getTotalCountryCount ();
    final ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; ++i)
    {
      final Country country = CountryFactory.create ("Country-" + i);
      countrySetBuilder.add (country);
    }
    return countrySetBuilder.build ();
  }

  private Optional <Country> getCountryByName (final String name)
  {
    assert name != null;
    assert !name.isEmpty ();

    if (!countryGraphModel.existsCountryWith (name)) return Optional.absent ();

    return Optional.of (countryGraphModel.modelCountryWith (name));
  }

  private ImmutableSet <Country> getOwnedCountries ()
  {
    final ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (final Id id : countryGraphModel.getCountryIds ())
    {
      if (countryIdsToOwnerIds.containsKey (id))
      {
        countrySetBuilder.add (countryGraphModel.modelCountryWith (id));
      }
    }
    return countrySetBuilder.build ();
  }

  // internal convenience method for running precondition check.
  private void checkValidCountryId (final Id countryId)
  {
    Preconditions.checkIsTrue (countryGraphModel.existsCountryWith (countryId), "Unrecognized country id.");
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Countries [{}] | CountryIdsToOwnerIds [{}]", getClass ().getSimpleName (),
                           countryGraphModel, countryIdsToOwnerIds);
  }
}
