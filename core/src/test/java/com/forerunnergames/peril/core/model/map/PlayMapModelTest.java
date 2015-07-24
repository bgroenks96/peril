package com.forerunnergames.peril.core.model.map;

import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class PlayMapModelTest
{
  private ImmutableSet <Country> defaultTestCountries;

  private static final int TEST_COUNTRY_COUNT = 20;

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
    this.defaultTestCountries = generateTestCountries (TEST_COUNTRY_COUNT);
  }

  @Test
  public void testRequestToAssignCountriesToSingleOwner ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testPlayer)).succeeded ());
      assertTrue (modelTest.ownerOf (idOf (testCountry)).is (idOf (testPlayer)));
    }
  }

  @Test
  public void testRequestToAssignCountriesToUniqueOwners ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      final Player testPlayer = PlayerFactory.create ("TestPlayer");
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testPlayer)).succeeded ());
      assertTrue (modelTest.ownerOf (idOf (testCountry)).is (idOf (testPlayer)));
    }
  }

  @Test
  public void testRequestToAssignCountryToOwnerFailsWithInvalidCountryId ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");

    final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
    // assign wrong id
    result = modelTest.requestToAssignCountryOwner (idOf (testPlayer), idOf (testPlayer));
    assertTrue (result.failedBecauseOf (PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToAssignCountryToOwnerFailsWithOwnedCountry ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer1 = PlayerFactory.create ("TestPlayer-1");
    final Country testCountry = defaultTestCountries.asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testPlayer1)).succeeded ());

    final Player testPlayer2 = PlayerFactory.create ("TestPlayer-2");

    final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
    result = modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testPlayer2));
    assertTrue (result.failedBecauseOf (PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_ALREADY_OWNED));
  }

  @Test
  public void testRequestToUnassignCountry ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final Country country = Randomness.getRandomElementFrom (defaultTestCountries);

    assertTrue (modelTest.requestToAssignCountryOwner (country.getId (), testPlayer.getId ()).succeeded ());
    assertTrue (modelTest.requestToUnassignCountry (country.getId ()).succeeded ());
    assertFalse (modelTest.isCountryOwned (country.getId ()));
  }

  @Test
  public void testRequestToUnassignCountryFailsWithInvalidCountryId ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");

    final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
    // assign wrong id
    result = modelTest.requestToUnassignCountry (idOf (testPlayer));
    assertTrue (result.failedBecauseOf (PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
  }

  @Test
  public void testCountryWithId ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.countryWith (testCountry.getId ()).is (testCountry));
    }
  }

  @Test
  public void testCountryWithName ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.countryWith (testCountry.getName ()).is (testCountry));
    }
  }

  @Test
  public void testExistsCountryWithId ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
    }
  }

  @Test
  public void testExistsCountryWithName ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (testCountry.getName ()));
    }
  }

  @Test
  public void testDoesNotExistsCountryWithName ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertFalse (modelTest.existsCountryWith ("invalid-name"));
  }

  @Test
  public void testGetOwnerOf ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Country testCountry = modelTest.getCountries ().asList ().get (0);
    final Player testOwner = PlayerFactory.create ("TestPlayer");

    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    assertEquals (idOf (testOwner), modelTest.ownerOf (idOf (testCountry)));
  }

  @Test (expected = IllegalStateException.class)
  public void testGetOwnerOfInvalidIdFailsWithException ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestPlayer");

    // request owner with invalid country Id
    modelTest.ownerOf (idOf (testOwner));
  }

  @Test
  public void testHasAnyUnownedCountriesNoneOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertFalse (modelTest.isCountryOwned (idOf (testCountry)));
    }
    assertTrue (modelTest.hasAnyUnownedCountries ());
  }

  @Test
  public void testHasAnyUnownedCountriesAllOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    }
    assertFalse (modelTest.hasAnyUnownedCountries ());
  }

  @Test
  public void testHasAnyUnownedCountriesOne ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    final int n = defaultTestCountries.size () - 1;
    final Iterator <Country> testCountriesItr = defaultTestCountries.iterator ();
    int i = 0;
    while (i < n)
    {
      final Country testCountry = testCountriesItr.next ();
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
      ++i;
    }
    assertTrue (modelTest.hasAnyUnownedCountries ());
  }

  @Test
  public void testHasAnyOwnedCountriesNoneOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertFalse (modelTest.isCountryOwned (idOf (testCountry)));
    }
    assertFalse (modelTest.hasAnyOwnedCountries ());
  }

  @Test
  public void testHasAnyOwnedCountriesOneOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    final Country testCountry = modelTest.getCountries ().asList ().get (0);
    assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    assertTrue (modelTest.hasAnyOwnedCountries ());
  }

  @Test
  public void testAllCountriesAreOwnedNoneOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertFalse (modelTest.isCountryOwned (idOf (testCountry)));
    }
    assertFalse (modelTest.allCountriesAreOwned ());
  }

  @Test
  public void testAllCountriesAreOwnedAllOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    }
    assertTrue (modelTest.allCountriesAreOwned ());
  }

  @Test
  public void testAllCountriesAreOwnedOneOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    final Country testCountry = modelTest.getCountries ().asList ().get (0);
    assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    assertFalse (modelTest.allCountriesAreOwned ());
  }

  @Test
  public void testAllCountriesAreUnownedNoneOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertFalse (modelTest.isCountryOwned (idOf (testCountry)));
    }
    assertTrue (modelTest.allCountriesAreUnowned ());
  }

  @Test
  public void testAllCountriesAreUnownedAllOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    }
    assertFalse (modelTest.allCountriesAreUnowned ());
  }

  @Test
  public void testAllCountriesAreUnownedOneOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    final Country testCountry = modelTest.getCountries ().asList ().get (0);
    assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    assertFalse (modelTest.allCountriesAreUnowned ());
  }

  @Test
  public void testGetCountryCount ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertEquals (defaultTestCountries.size (), modelTest.getCountryCount ());
  }

  @Test
  public void testGetCountryCountIsSizeOfDefault ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIs (defaultTestCountries.size ()));
  }

  @Test
  public void testGetCountryCountIsFalseOnCountPlusOne ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertFalse (modelTest.countryCountIs (defaultTestCountries.size () + 1));
  }

  @Test
  public void testGetCountryCountIsAtLeastSizeOfDefault ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIsAtLeast (defaultTestCountries.size ()));
  }

  @Test
  public void testGetCountryCountIsAtLeastSizeOfDefaultMinusOne ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIsAtLeast (defaultTestCountries.size () - 1));
  }

  @Test
  public void testGetCountryCountIsAtLeastFalseForSizeOfDefaultPlusOne ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertFalse (modelTest.countryCountIsAtLeast (defaultTestCountries.size () + 1));
  }

  @Test
  public void testGetOwnedCountryCountHalfCountriesOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestPlayer");
    final int n = modelTest.getCountryCount () / 2;

    int i = 0;
    final Iterator <Country> countryItr = modelTest.getCountries ().iterator ();
    while (i < n && countryItr.hasNext ())
    {
      final Country nextCountry = countryItr.next ();
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (nextCountry), idOf (testOwner)).succeeded ());
      ++i;
    }

    assertEquals (n, modelTest.getOwnedCountryCount ());
  }

  @Test
  public void testOwnedCountryCountIsZero ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    assertTrue (modelTest.allCountriesAreUnowned ());

    assertTrue (modelTest.ownedCountryCountIs (0));
  }

  @Test
  public void testOwnedCountryCountIsOne ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestPlayer");
    final Country testCountry = modelTest.getCountries ().asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());

    assertTrue (modelTest.ownedCountryCountIs (1));
  }

  @Test
  public void testOwnedCountryCountIsAtLeastOne ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestPlayer");
    final Country testCountry = modelTest.getCountries ().asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());

    assertTrue (modelTest.ownedCountryCountIsAtLeast (1));
  }

  @Test
  public void testOwnedCountryCountIsAtLeastTwoAllOwned ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    }
    assertTrue (modelTest.ownedCountryCountIsAtLeast (2));
  }

  @Test
  public void testOwnedCountryCountIsNotAtLeastTwo ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestPlayer");
    final Country testCountry = modelTest.getCountries ().asList ().get (0);

    assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());

    assertFalse (modelTest.ownedCountryCountIsAtLeast (2));
  }

  private static PlayMapModel createPlayMapModelTestWith (final ImmutableSet <Country> countries)
  {
    final GameRules classicRules = new ClassicGameRules.Builder ().build ();
    return new DefaultPlayMapModel (countries, ImmutableSet.<Continent> of (), classicRules);
  }
}
