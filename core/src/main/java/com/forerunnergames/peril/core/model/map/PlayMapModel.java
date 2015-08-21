package com.forerunnergames.peril.core.model.map;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface PlayMapModel
{
  boolean existsCountryWith (final Id countryId);

  Country countryWith (final Id countryId);

  boolean existsCountryWith (final String countryName);

  Country countryWith (final String countryName);

  boolean existsContinentWith (final Id id);

  Continent continentWith (final Id id);

  boolean existsContinentWith (final String continentName);

  Continent continentWith (final String continentName);

  boolean hasAnyUnownedCountries ();

  boolean allCountriesAreUnowned ();

  boolean hasAnyOwnedCountries ();

  boolean allCountriesAreOwned ();

  boolean isCountryOwned (final Id countryId);

  boolean isCountryOwned (final String countryName);

  /**
   * Assigns the Country specified by countryId to the owner specified by ownerId.
   *
   * @return success/failure Result with reason
   */
  Result <PlayerSelectCountryResponseDeniedEvent.Reason> requestToAssignCountryOwner (final Id countryId,
                                                                                      final Id ownerId);

  /**
   * Unassigns the Country specified by the given id from its current owner.
   *
   * @return success/failure Result
   */
  Result <PlayerSelectCountryResponseDeniedEvent.Reason> requestToUnassignCountry (final Id countryId);

  /**
   * @return the Id of the owner to which the Country specified by countryId is currently assigned.
   */
  Id ownerOf (final Id countryId);

  ImmutableSet <Country> getCountries ();

  ImmutableSet <Country> getCountriesOwnedBy (final Id ownerId);

  ImmutableSet <String> getCountryNamesOwnedBy (final Id ownerId);

  void unassignAllCountries ();

  void unassignAllCountriesOwnedBy (final Id ownerId);

  int countCountriesOwnedBy (final Id ownerId);

  int getCountryCount ();

  boolean countryCountIs (final int n);

  boolean countryCountIsAtLeast (final int n);

  int getOwnedCountryCount ();

  boolean ownedCountryCountIs (final int n);

  boolean ownedCountryCountIsAtLeast (final int n);
}
