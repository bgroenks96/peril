package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;

public final class DefaultCountryOwnerModel implements CountryOwnerModel
{
  private final CountryMapGraphModel countryMapGraphModel;
  private final Map <Id, Id> countryIdsToOwnerIds = new HashMap <> ();

  public DefaultCountryOwnerModel (final CountryMapGraphModel countryMapGraphModel, final GameRules rules)
  {
    Arguments.checkIsNotNull (countryMapGraphModel, "countryMapGraphModel");
    Arguments.checkIsNotNull (rules, "rules");
    Preconditions.checkIsTrue (countryMapGraphModel.size () >= rules.getMinTotalCountryCount (), "Country count of "
            + countryMapGraphModel.size () + " is below minimum of " + rules.getMinTotalCountryCount () + "!");
    Preconditions.checkIsTrue (countryMapGraphModel.size () <= rules.getMaxTotalCountryCount (), "Country count "
            + countryMapGraphModel.size () + " is above maximum of " + rules.getMaxTotalCountryCount () + "!");

    this.countryMapGraphModel = countryMapGraphModel;
  }

  @Override
  public boolean hasAnyUnownedCountries ()
  {
    // if the country -> owner map is less than the size of the country ID map, then there must be
    // some countries without an assigned owner.
    return countryIdsToOwnerIds.size () < countryMapGraphModel.size ();
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
    return countryIdsToOwnerIds.size () == countryMapGraphModel.size ();
  }

  @Override
  public boolean isCountryOwned (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Preconditions.checkIsTrue (countryMapGraphModel.existsCountryWith (countryId),
                               Strings.format ("No country with Id [{}] exists.", countryId));

    return countryIdsToOwnerIds.containsKey (countryId);
  }

  @Override
  public boolean isCountryOwned (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Optional <Country> country = getCountryByName (countryName);
    Preconditions.checkIsTrue (country.isPresent (), Strings.format ("No country with name [{}] exists.", countryName));

    return countryMapGraphModel.existsCountryWith (countryName) && isCountryOwned (country.get ().getId ());
  }

  @Override
  public boolean isCountryOwnedBy (final Id countryId, final Id playerId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNull (playerId, "playerId");

    return countryMapGraphModel.existsCountryWith (countryId) && isCountryOwned (countryId)
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
  public Result <AbstractCountryStateChangeDeniedEvent.Reason> requestToAssignCountryOwner (final Id countryId,
                                                                                            final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");
    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!countryMapGraphModel.existsCountryWith (countryId)) return Result.failure (Reason.COUNTRY_DOES_NOT_EXIST);
    if (isCountryOwned (countryId)) return Result.failure (Reason.COUNTRY_ALREADY_OWNED);
    //@formatter:on

    countryIdsToOwnerIds.put (countryId, ownerId);
    return Result.success ();
  }

  /**
   * Assigns the Country specified by countryId to the owner specified by ownerId.
   *
   * @return success/failure Result with reason
   */
  @Override
  public Result <PlayerOccupyCountryResponseDeniedEvent.Reason> requestToReassignCountryOwner (final Id countryId,
                                                                                               final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");
    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!countryMapGraphModel.existsCountryWith (countryId)) return Result.failure (Reason.COUNTRY_DOES_NOT_EXIST);
    if (isCountryOwnedBy (countryId, ownerId)) return Result.failure (Reason.COUNTRY_ALREADY_OWNED);
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
  public Result <AbstractCountryStateChangeDeniedEvent.Reason> requestToUnassignCountry (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    //@formatter:off
    if (!countryMapGraphModel.existsCountryWith (countryId)) return Result.failure (Reason.COUNTRY_DOES_NOT_EXIST);
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
  public ImmutableSet <CountryPacket> getCountriesOwnedBy (final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");

    if (!countryIdsToOwnerIds.containsValue (ownerId)) return ImmutableSet.of ();

    final ImmutableSet.Builder <CountryPacket> countryBuilder = ImmutableSet.builder ();

    for (final Map.Entry <Id, Id> countryIdToOwnerIdEntry : countryIdsToOwnerIds.entrySet ())
    {
      if (!countryIdToOwnerIdEntry.getValue ().equals (ownerId)) continue;

      final Country country = countryMapGraphModel.modelCountryWith (countryIdToOwnerIdEntry.getKey ());

      if (country != null) countryBuilder.add (CountryPackets.from (country));
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

      final Country country = countryMapGraphModel.modelCountryWith (countryIdToOwnerIdEntry.getKey ());

      if (country != null) countryNameBuilder.add (country.getName ());
    }

    return countryNameBuilder.build ();
  }

  @Override
  public ImmutableSet <CountryPacket> getUnownedCountries ()
  {
    final ImmutableSet.Builder <CountryPacket> unownedCountries = ImmutableSet.builder ();
    for (final Id countryId : countryMapGraphModel)
    {
      if (!isCountryOwned (countryId)) unownedCountries.add (countryMapGraphModel.countryPacketWith (countryId));
    }
    return unownedCountries.build ();
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

    for (final Country country : countryMapGraphModel.getCountries ())
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

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Countries [{}] | CountryIdsToOwnerIds [{}]", getClass ().getSimpleName (),
                           countryMapGraphModel, countryIdsToOwnerIds);
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

    if (!countryMapGraphModel.existsCountryWith (name)) return Optional.absent ();

    return Optional.of (countryMapGraphModel.modelCountryWith (name));
  }

  private ImmutableSet <Country> getOwnedCountries ()
  {
    final ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (final Id id : countryMapGraphModel.getCountryIds ())
    {
      if (countryIdsToOwnerIds.containsKey (id))
      {
        countrySetBuilder.add (countryMapGraphModel.modelCountryWith (id));
      }
    }
    return countrySetBuilder.build ();
  }

  // internal convenience method for running precondition check.
  private void checkValidCountryId (final Id countryId)
  {
    Preconditions.checkIsTrue (countryMapGraphModel.existsCountryWith (countryId), "Unrecognized country id.");
  }
}
