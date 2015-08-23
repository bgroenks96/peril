package com.forerunnergames.peril.core.model.map;

import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;

public final class DefaultPlayMapModel implements PlayMapModel
{
  private final Map <Id, Country> countryIdsToCountries;
  private final Map <Id, Continent> continentIdsToContinents;
  private final Map <Id, Id> countryIdsToOwnerIds = new HashMap <> ();

  public DefaultPlayMapModel (final ImmutableSet <Country> countries,
                              final ImmutableSet <Continent> continents,
                              final GameRules rules)
  {
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkIsNotNull (continents, "continents");
    Arguments.checkHasNoNullElements (countries, "countries");
    Arguments.checkIsNotNull (rules, "rules");
    Preconditions.checkIsTrue (countries.size () >= rules.getMinTotalCountryCount (), "Country count of "
            + countries.size () + " is below minimum of " + rules.getMinTotalCountryCount () + "!");
    Preconditions.checkIsTrue (countries.size () <= rules.getMaxTotalCountryCount (), "Country count "
            + countries.size () + " is above maximum of " + rules.getMaxTotalCountryCount () + "!");

    // init country id map
    final Builder <Id, Country> countryMapBuilder = ImmutableMap.builder ();
    for (final Country country : countries)
    {
      countryMapBuilder.put (country.getId (), country);
    }
    countryIdsToCountries = countryMapBuilder.build ();

    // init continent id map
    final Builder <Id, Continent> continentMapBuilder = ImmutableMap.builder ();
    for (final Continent continent : continents)
    {
      continentMapBuilder.put (continent.getId (), continent);
    }
    continentIdsToContinents = continentMapBuilder.build ();
  }

  @Override
  public boolean existsCountryWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    return countryIdsToCountries.containsKey (countryId);
  }

  @Override
  public Country countryWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    checkValidCountryId (countryId);

    return countryIdsToCountries.get (countryId);
  }

  @Override
  public boolean existsCountryWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return getCountryByName (countryName).isPresent ();
  }

  @Override
  public Country countryWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Optional <Country> country = getCountryByName (countryName);
    if (!country.isPresent ()) Exceptions.throwIllegalArg ("Cannot find any country named: [{}].", countryName);
    return country.get ();
  }

  @Override
  public boolean existsContinentWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    return continentIdsToContinents.get (id) != null;
  }

  @Override
  public Continent continentWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    final Continent continent = continentIdsToContinents.get (id);
    if (continent == null) Exceptions.throwIllegalArg ("Cannot find any continent with Id: [{}].", id);
    return continent;
  }

  @Override
  public boolean existsContinentWith (final String continentName)
  {
    Arguments.checkIsNotNull (continentName, "continentName");

    return getCountryByName (continentName) != null;
  }

  @Override
  public Continent continentWith (final String continentName)
  {
    Arguments.checkIsNotNull (continentName, "continentName");

    final Optional <Continent> continent = getContinentByName (continentName);
    if (!continent.isPresent ())
    {
      Exceptions.throwIllegalArg ("Cannot find any continent named: [" + continentName + "].");
    }
    return continent.get ();
  }

  @Override
  public boolean hasAnyUnownedCountries ()
  {
    // if the country -> owner map is less than the size of the country ID map, then there must be
    // some countries without an assigned owner.
    return countryIdsToOwnerIds.size () < countryIdsToCountries.size ();
  }

  @Override
  public boolean allCountriesAreUnowned ()
  {
    return countryIdsToOwnerIds.size () == 0;
  }

  @Override
  public boolean hasAnyOwnedCountries ()
  {
    return countryIdsToOwnerIds.size () > 0;
  }

  @Override
  public boolean allCountriesAreOwned ()
  {
    return countryIdsToOwnerIds.size () == countryIdsToCountries.size ();
  }

  @Override
  public boolean isCountryOwned (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    return countryIdsToOwnerIds.containsKey (countryId);
  }

  @Override
  public boolean isCountryOwned (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return existsCountryWith (countryName) && isCountryOwned (idOf (countryWith (countryName)));
  }

  /**
   * Assigns the Country specified by countryId to the owner specified by ownerId.
   *
   * @return success/failure Result with reason
   */
  @Override
  public Result <PlayerSelectCountryResponseDeniedEvent.Reason> requestToAssignCountryOwner (final Id countryId,
                                                                                             final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");

    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!existsCountryWith (countryId)) return Result.failure (PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST);
    if (isCountryOwned (countryId)) return Result.failure (PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_ALREADY_OWNED);
    //@formatter:on

    countryIdsToOwnerIds.put (countryId, ownerId);
    return Result.success ();
  }

  /**
   * Unassigns the Country specified by the given id from its current owner.
   *
   * @return success/failure Result
   */
  @Override
  public Result <PlayerSelectCountryResponseDeniedEvent.Reason> requestToUnassignCountry (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!existsCountryWith (countryId)) return Result.failure (PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST);
    //@formatter:on

    countryIdsToOwnerIds.remove (countryId);
    return Result.success ();
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
  public ImmutableSet <Country> getCountries ()
  {
    return ImmutableSet.copyOf (countryIdsToCountries.values ());
  }

  @Override
  public ImmutableSet <Country> getCountriesOwnedBy (final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");

    if (!countryIdsToOwnerIds.containsValue (ownerId)) return ImmutableSet.of ();

    final ImmutableSet.Builder <Country> countryBuilder = ImmutableSet.builder ();

    for (final Map.Entry <Id, Id> countryIdToOwnerIdEntry : countryIdsToOwnerIds.entrySet ())
    {
      if (!countryIdToOwnerIdEntry.getValue ().equals (ownerId)) continue;

      final Country country = countryIdsToCountries.get (countryIdToOwnerIdEntry.getKey ());

      if (country != null) countryBuilder.add (country);
    }

    return countryBuilder.build ();
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

      final Country country = countryIdsToCountries.get (countryIdToOwnerIdEntry.getKey ());

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

    for (final Country country : countryIdsToCountries.values ())
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
  public int getCountryCount ()
  {
    return countryIdsToCountries.size ();
  }

  @Override
  public boolean countryCountIs (final int n)
  {
    Arguments.checkIsNotNegative (n, "n");

    return getCountryCount () == n;
  }

  @Override
  public boolean countryCountIsAtLeast (final int n)
  {
    Arguments.checkIsNotNegative (n, "n");

    return getCountryCount () >= n;
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

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Countries [{}] | Continents [{}] | CountryIdsToOwnerIds [{}]",
                           getClass ().getSimpleName (), countryIdsToCountries.values (),
                           continentIdsToContinents.values (), countryIdsToOwnerIds);
  }

  private Optional <Country> getCountryByName (final String name)
  {
    assert name != null;
    //@formatter:off
    assert !name.isEmpty ();
    //@formmatter:on

    for (final Map.Entry <Id, Country> idCountryEntry : countryIdsToCountries.entrySet ())
    {
      final Country country = idCountryEntry.getValue ();
      if (country.hasName (name)) return Optional.of (country);
    }
    return Optional.absent ();
  }

  private Optional <Continent> getContinentByName (final String name)
  {
    assert name != null;
    //@formatter:off
    assert !name.isEmpty ();
    //@formmatter:on

    for (final Map.Entry <Id, Continent> idCountryEntry : continentIdsToContinents.entrySet ())
    {
      final Continent continent = idCountryEntry.getValue ();
      if (continent.hasName (name)) return Optional.of (continent);
    }
    return Optional.absent ();
  }

  private ImmutableSet <Country> getOwnedCountries ()
  {
    final ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (final Id id : countryIdsToCountries.keySet ())
    {
      if (countryIdsToOwnerIds.containsKey (id))
      {
        countrySetBuilder.add (countryIdsToCountries.get (id));
      }
    }
    return countrySetBuilder.build ();
  }

  // internal convenience method for running precondition check.
  private void checkValidCountryId (final Id countryId)
  {
    Preconditions.checkIsTrue (countryIdsToCountries.containsKey (countryId), "Unrecognized country id.");
  }

  public static ImmutableSet <Country> generateDefaultCountries (final GameRules gameRules)
  {
    Arguments.checkIsNotNull (gameRules, "gameRules");

    final int count = gameRules.getMinTotalCountryCount ();
    final ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; ++i)
    {
      final Country country = CountryFactory.create ("Country-" + i);
      countrySetBuilder.add (country);
    }
    return countrySetBuilder.build ();
  }
}
