package com.forerunnergames.peril.core.model.map;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;

public final class PlayMapModel
{
  private final Map <Id, Country> countryIds;
  private final Map <Id, Id> countryToOwnerMap;

  public PlayMapModel (final ImmutableSet <Country> countries, final GameRules rules)
  {
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");
    Arguments.checkIsNotNull (rules, "rules");
    Preconditions.checkIsTrue (countries.size () >= rules.getMinTotalCountryCount (),
                               "Country count is below minimum allowed!");
    Preconditions.checkIsTrue (countries.size () <= rules.getMaxTotalCountryCount (),
                               "Country count is above maximum allowed!");

    // init country id map
    final Builder <Id, Country> countryMapBuilder = ImmutableMap.builder ();
    for (final Country country : countries)
    {
      countryMapBuilder.put (country.getId (), country);
    }
    countryIds = countryMapBuilder.build ();

    countryToOwnerMap = new HashMap <> ();
  }

  public boolean existsCountryWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    return countryIds.containsKey (countryId);
  }

  public Country countryWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    checkValidCountryId (countryId);

    return countryIds.get (countryId);
  }

  public boolean existsCountryWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return getCountryByName (countryName) != null;
  }

  public Country countryWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Country country = getCountryByName (countryName);
    if (country == null) throw new IllegalStateException ("Cannot find any country named: [" + countryName + "].");
    return country;
  }

  public boolean hasUnassignedCountries ()
  {
    // if the country -> owner map is less than the size of the country ID map, then there must be
    // some countries without an assigned owner.
    return countryToOwnerMap.size () < countryIds.size ();
  }

  public boolean isCountryAssigned (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    return countryToOwnerMap.containsKey (countryId);
  }

  /**
   * Assigns the Country specified by countryId to the owner specified by ownerId.
   *
   * @param countryId
   * @param ownerId
   * @return the Id of the owner to which the Country was previously assigned, if any.
   */
  public Id assignCountryOwner (final Id countryId, final Id ownerId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    checkValidCountryId (countryId);

    return countryToOwnerMap.put (countryId, ownerId);
  }

  /**
   * Unassigns the Country specified by the given id from its current owner.
   *
   * @param countryId
   * @return the Id of the owner to which the Country was previously assigned, if any.
   */
  public Id unassignCountry (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    checkValidCountryId (countryId);

    return countryToOwnerMap.remove (countryId);
  }

  /**
   * @param countryId
   * @return the Id of the owner to which the Country specified by countryId is currently assigned.
   */
  public Id getOwnerOf (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    checkValidCountryId (countryId);

    if (isCountryAssigned (countryId)) return countryToOwnerMap.get (countryId);
    else throw new IllegalStateException ("Country with id [" + countryId + "] has no owner.");
  }

  public ImmutableSet <Country> getCountries ()
  {
    return ImmutableSet.copyOf (countryIds.values ());
  }

  public int getCountryCount ()
  {
    return countryIds.size ();
  }

  public int getAssignedCountryCount ()
  {
    return getAssignedCountries ().size ();
  }

  private Country getCountryByName (final String name)
  {
    assert name != null;
    assert !name.isEmpty ();

    for (final Id nextId : countryIds.keySet ())
    {
      final Country country = countryIds.get (nextId);
      if (country.hasName (name)) return country;
    }
    return null;
  }

  private ImmutableSet <Country> getAssignedCountries ()
  {
    final ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (final Id id : countryIds.keySet ())
    {
      if (countryToOwnerMap.containsKey (id))
      {
        countrySetBuilder.add (countryIds.get (id));
      }
    }
    return countrySetBuilder.build ();
  }

  // internal convenience method for running precondition check.
  private void checkValidCountryId (final Id countryId)
  {
    Preconditions.checkIsTrue (countryIds.containsKey (countryId), "Unrecognized country id.");
  }
}
