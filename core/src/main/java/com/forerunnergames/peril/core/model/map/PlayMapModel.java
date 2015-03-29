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
  private final GameRules rules;

  public PlayMapModel (final ImmutableSet <Country> countries, final GameRules rules)
  {
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;

    // init country id map
    Builder <Id, Country> countryMapBuilder = ImmutableMap.builder ();
    for (Country country : countries)
    {
      countryMapBuilder.put (country.getId (), country);
    }
    countryIds = countryMapBuilder.build ();

    countryToOwnerMap = new HashMap <> ();
  }

  public Country countryWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    checkValidCountryId (countryId);

    return countryIds.get (countryId);
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

    return countryToOwnerMap.get (countryId);
  }

  public ImmutableSet <Country> getCountries ()
  {
    return ImmutableSet.copyOf (countryIds.values ());
  }

  public ImmutableSet <Country> getAssignedCountries ()
  {
    ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (Id id : countryIds.keySet ())
    {
      if (countryToOwnerMap.containsKey (id)) countrySetBuilder.add (countryIds.get (id));
    }
    return countrySetBuilder.build ();
  }

  // internal convenience method for running precondition check.
  private void checkValidCountryId (final Id countryId)
  {
    Preconditions.checkIsTrue (countryIds.containsKey (countryId), "Unrecognized country id.");
  }
}
