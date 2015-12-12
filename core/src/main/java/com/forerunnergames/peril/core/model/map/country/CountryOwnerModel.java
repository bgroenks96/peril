package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangeDeniedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Result;
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
  Result <CountryOwnerChangeDeniedEvent.Reason> requestToAssignCountryOwner (final Id countryId, final Id ownerId);

  /**
   * Unassigns the Country specified by the given id from its current owner.
   *
   * @return success/failure Result
   */
  Result <CountryOwnerChangeDeniedEvent.Reason> requestToUnassignCountry (final Id countryId);

  Id ownerOf (final Id countryId);

  ImmutableSet <CountryPacket> getCountriesOwnedBy (final Id ownerId);

  ImmutableSet <String> getCountryNamesOwnedBy (final Id ownerId);

  void unassignAllCountries ();

  void unassignAllCountriesOwnedBy (final Id ownerId);

  int countCountriesOwnedBy (final Id ownerId);

  int getOwnedCountryCount ();

  boolean ownedCountryCountIs (final int n);

  boolean ownedCountryCountIsAtLeast (final int n);
}
