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

package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

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
  MutatorResult <AbstractCountryStateChangeDeniedEvent.Reason> requestToAssignCountryOwner (final Id countryId,
                                                                                            final Id ownerId);

  MutatorResult <AbstractCountryStateChangeDeniedEvent.Reason> requestToReassignCountryOwner (final Id countryId,
                                                                                              final Id newOwner);

  /**
   * Unassigns the Country specified by the given id from its current owner.
   *
   * @return success/failure Result
   */
  MutatorResult <AbstractCountryStateChangeDeniedEvent.Reason> requestToUnassignCountry (final Id countryId);

  Id ownerOf (final Id countryId);

  ImmutableSet <CountryPacket> getCountriesOwnedBy (final Id ownerId);

  ImmutableSet <CountryPacket> getUnownedCountries ();

  ImmutableSet <String> getCountryNamesOwnedBy (final Id ownerId);

  void unassignAllCountries ();

  void unassignAllCountriesOwnedBy (final Id ownerId);

  int countCountriesOwnedBy (final Id ownerId);

  int getOwnedCountryCount ();

  boolean ownedCountryCountIs (final int n);

  boolean ownedCountryCountIsAtLeast (final int n);
}
