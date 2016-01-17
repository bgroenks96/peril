package com.forerunnergames.peril.core.model.map.country;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class CountryOwnerModelTest
{
  private static final int TEST_COUNTRY_COUNT = 20;
  private CountryMapGraphModel countryMapGraphModel;
  private ImmutableSet <Country> defaultTestCountries;

  public static ImmutableSet <Country> generateTestCountries (final int count)
  {
    final Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; ++i)
    {
      final Country country = CountryFactory.create ("Country-" + i);
      countrySetBuilder.add (country);
    }
    return countrySetBuilder.build ();
  }

  @Before
  public void setup ()
  {
    defaultTestCountries = generateTestCountries (TEST_COUNTRY_COUNT);
    countryMapGraphModel = CountryMapGraphModelTest.createDisjointCountryMapGraphModelWith (defaultTestCountries);
  }

  @Test
  public void testRequestToAssignCountriesToSingleOwner ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
      assertTrue (modelTest.ownerOf (testCountryId).is (testPlayerId));
    }
  }

  @Test
  public void testRequestToAssignCountriesToUniqueOwners ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);

    for (final Id testCountryId : countryMapGraphModel)
    {
      final Id testPlayerId = IdGenerator.generateUniqueId ();
      assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
      assertTrue (modelTest.ownerOf (testCountryId).is (testPlayerId));
    }
  }

  @Test
  public void testRequestToAssignCountryToOwnerFailsWithInvalidCountryId ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    final Result <PlayerClaimCountryResponseDeniedEvent.Reason> result;
    // assign wrong id
    result = modelTest.requestToAssignCountryOwner (testPlayerId, testPlayerId);
    assertTrue (result.failedBecauseOf (PlayerClaimCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToAssignCountryToOwnerFailsWithOwnedCountry ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId_1 = IdGenerator.generateUniqueId ();
    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId_1).succeeded ());

    final Id testPlayerId_2 = IdGenerator.generateUniqueId ();

    final Result <PlayerClaimCountryResponseDeniedEvent.Reason> result;
    result = modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId_2);
    assertTrue (result.failedBecauseOf (PlayerClaimCountryResponseDeniedEvent.Reason.COUNTRY_ALREADY_CLAIMED));
  }

  @Test
  public void testRequestToUnassignCountry ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();
    final Country country = Randomness.getRandomElementFrom (countryMapGraphModel.getCountries ());

    assertTrue (modelTest.requestToAssignCountryOwner (country.getId (), testPlayerId).succeeded ());
    assertTrue (modelTest.requestToUnassignCountry (country.getId ()).succeeded ());
    assertFalse (modelTest.isCountryOwned (country.getId ()));
  }

  @Test
  public void testRequestToUnassignCountryFailsWithInvalidCountryId ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    final Result <PlayerClaimCountryResponseDeniedEvent.Reason> result;
    // assign wrong id
    result = modelTest.requestToUnassignCountry (testPlayerId);
    assertTrue (result.failedBecauseOf (PlayerClaimCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
  }

  @Test
  public void testGetOwnerOf ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    assertEquals (testPlayerId, modelTest.ownerOf (testCountryId));
  }

  @Test (expected = IllegalStateException.class)
  public void testGetOwnerOfInvalidIdFailsWithException ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    // request owner with invalid country Id
    modelTest.ownerOf (testPlayerId);
  }

  @Test
  public void testHasAnyUnownedCountriesNoneOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
      assertFalse (modelTest.isCountryOwned (testCountryId));
    }
    assertTrue (modelTest.hasAnyUnownedCountries ());
  }

  @Test
  public void testHasAnyUnownedCountriesAllOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
      assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    }
    assertFalse (modelTest.hasAnyUnownedCountries ());
  }

  @Test
  public void testHasAnyUnownedCountriesOne ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    final int n = countryMapGraphModel.size () - 1;
    final Iterator <Country> testCountriesItr = countryMapGraphModel.getCountries ().iterator ();
    int i = 0;
    while (i < n)
    {
      final Country testCountry = testCountriesItr.next ();
      assertTrue (countryMapGraphModel.existsCountryWith (testCountry.getId ()));
      assertTrue (modelTest.requestToAssignCountryOwner (testCountry.getId (), testPlayerId).succeeded ());
      ++i;
    }
    assertTrue (modelTest.hasAnyUnownedCountries ());
  }

  @Test
  public void testHasAnyOwnedCountriesNoneOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
      assertFalse (modelTest.isCountryOwned (testCountryId));
    }
    assertFalse (modelTest.hasAnyOwnedCountries ());
  }

  @Test
  public void testHasAnyOwnedCountriesOneOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);
    assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    assertTrue (modelTest.hasAnyOwnedCountries ());
  }

  @Test
  public void testAllCountriesAreOwnedNoneOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
      assertFalse (modelTest.isCountryOwned (testCountryId));
    }
    assertFalse (modelTest.allCountriesAreOwned ());
  }

  @Test
  public void testAllCountriesAreOwnedAllOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
      assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    }
    assertTrue (modelTest.allCountriesAreOwned ());
  }

  @Test
  public void testAllCountriesAreOwnedOneOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);
    assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    assertFalse (modelTest.allCountriesAreOwned ());
  }

  @Test
  public void testAllCountriesAreUnownedNoneOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
      assertFalse (modelTest.isCountryOwned (testCountryId));
    }
    assertTrue (modelTest.allCountriesAreUnowned ());
  }

  @Test
  public void testAllCountriesAreUnownedAllOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
      assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    }
    assertFalse (modelTest.allCountriesAreUnowned ());
  }

  @Test
  public void testAllCountriesAreUnownedOneOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);
    assertTrue (countryMapGraphModel.existsCountryWith (testCountryId));
    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    assertFalse (modelTest.allCountriesAreUnowned ());
  }

  @Test
  public void testGetOwnedCountryCountHalfCountriesOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();
    final int n = countryMapGraphModel.getCountryCount () / 2;

    int i = 0;
    final Iterator <Country> countryItr = countryMapGraphModel.getCountries ().iterator ();
    while (i < n && countryItr.hasNext ())
    {
      final Country nextCountry = countryItr.next ();
      assertTrue (modelTest.requestToAssignCountryOwner (nextCountry.getId (), testPlayerId).succeeded ());
      ++i;
    }

    assertEquals (n, modelTest.getOwnedCountryCount ());
  }

  @Test
  public void testOwnedCountryCountIsZero ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);

    assertTrue (modelTest.allCountriesAreUnowned ());

    assertTrue (modelTest.ownedCountryCountIs (0));
  }

  @Test
  public void testOwnedCountryCountIsOne ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();
    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());

    assertTrue (modelTest.ownedCountryCountIs (1));
  }

  @Test
  public void testOwnedCountryCountIsAtLeastOne ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();
    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());

    assertTrue (modelTest.ownedCountryCountIsAtLeast (1));
  }

  @Test
  public void testOwnedCountryCountIsAtLeastTwoAllOwned ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();

    for (final Id testCountryId : countryMapGraphModel)
    {
      assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());
    }
    assertTrue (modelTest.ownedCountryCountIsAtLeast (2));
  }

  @Test
  public void testOwnedCountryCountIsNotAtLeastTwo ()
  {
    final CountryOwnerModel modelTest = createDefaultCountryOwnerModelWith (countryMapGraphModel);
    final Id testPlayerId = IdGenerator.generateUniqueId ();
    final Id testCountryId = countryMapGraphModel.getCountryIds ().asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (testCountryId, testPlayerId).succeeded ());

    assertFalse (modelTest.ownedCountryCountIsAtLeast (2));
  }

  private static CountryOwnerModel createDefaultCountryOwnerModelWith (final CountryMapGraphModel countryMapGraphModel)
  {
    final GameRules classicRules = new ClassicGameRules.Builder ().build ();
    return new DefaultCountryOwnerModel (countryMapGraphModel, classicRules);
  }
}
