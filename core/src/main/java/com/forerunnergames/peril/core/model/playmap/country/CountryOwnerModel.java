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

package com.forerunnergames.peril.core.model.playmap.country;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;

import javax.annotation.Nullable;

public interface CountryOwnerModel
{
  boolean hasAnyUnownedCountries ();

  boolean allCountriesAreUnowned ();

  boolean hasAnyOwnedCountries ();

  boolean allCountriesAreOwned ();

  boolean isCountryOwned (final Id countryId);

  boolean isCountryOwned (final String countryName);

  boolean isCountryOwnedBy (final Id countryId, final Id ownerId);

  /**
   * Assigns the Country specified by countryId to the owner specified by ownerId.
   *
   * @return success/failure Result with reason
   */
  MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> requestToAssignCountryOwner (final Id countryId,
                                                                                             final Id ownerId);

  MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> requestToReassignCountryOwner (final Id countryId,
                                                                                               final Id newOwner);

  /**
   * Unassigns the Country specified by the given id from its current owner.
   *
   * @return success/failure Result
   */
  MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> requestToUnassignCountry (final Id countryId);

  Id ownerOf (final Id countryId);

  ImmutableSet <CountryPacket> getCountryPacketsOwnedBy (final Id ownerId);

  ImmutableSet <Id> getCountriesOwnedBy (final Id ownerId);

  ImmutableSet <CountryPacket> getUnownedCountries ();

  ImmutableSet <String> getCountryNamesOwnedBy (final Id ownerId);

  void unassignAllCountries ();

  /**
   *
   * @param ownerPacket
   *          The {@link PlayerPacket} represented by the specified ownerId. Used to pass the previous owner to the
   *          {@link CountryOwnerMutation} set. The value of the specified owner {@link Id} must match that of the
   *          specified owner {@link PlayerPacket}.
   */
  ImmutableSet <CountryOwnerMutation> unassignAllCountriesOwnedBy (final Id ownerId, final PlayerPacket ownerPacket);

  int countCountriesOwnedBy (final Id ownerId);

  int getOwnedCountryCount ();

  boolean ownedCountryCountIs (final int n);

  boolean ownedCountryCountIsAtLeast (final int n);

  final class CountryOwnerMutation
  {
    private final CountryPacket country;
    @Nullable
    private final PlayerPacket previousOwner;
    @Nullable
    private final PlayerPacket newOwner;

    CountryOwnerMutation (final CountryPacket country,
                          @Nullable final PlayerPacket previousOwner,
                          @Nullable final PlayerPacket newOwner)
    {
      Arguments.checkIsNotNull (country, "country");
      Arguments
              .checkIsFalse (Objects.equals (previousOwner, newOwner),
                             "previousOwner [{}] and newOwner [{}] cannot be the same (otherwise no mutation occurred).");

      this.country = country;
      this.previousOwner = previousOwner;
      this.newOwner = newOwner;
    }

    @Override
    public int hashCode ()
    {
      return country.hashCode ();
    }

    @Override
    public boolean equals (final Object o)
    {
      if (this == o) return true;
      if (o == null || getClass () != o.getClass ()) return false;

      final CountryOwnerMutation mutation = (CountryOwnerMutation) o;

      return country.equals (mutation.country);
    }

    @Override
    public String toString ()
    {
      return Strings.format ("{}: Country: [{}] | PreviousOwner: [{}] | NewOwner: [{}]", getClass ().getSimpleName (),
                             country, previousOwner, newOwner);
    }

    public CountryPacket getCountry ()
    {
      return country;
    }

    public boolean hasPreviousOwnerId ()
    {
      return previousOwner != null;
    }

    @Nullable
    public PlayerPacket getPreviousOwner ()
    {
      return previousOwner;
    }

    public boolean hasNewOwnerId ()
    {
      return newOwner != null;
    }

    @Nullable
    public PlayerPacket getNewOwner ()
    {
      return newOwner;
    }
  }
}
