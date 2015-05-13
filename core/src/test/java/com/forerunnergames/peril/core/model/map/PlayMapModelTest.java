package com.forerunnergames.peril.core.model.map;

import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.tools.common.Randomness;

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
    for (int i = 0; i < count; i++)
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
  public void testAssignCountriesToSingleOwner ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");

    for (final Country testCountry : defaultTestCountries)
    {
      modelTest.requestToAssignCountryOwner (testCountry.getId (), testPlayer.getId ());
      assertTrue (modelTest.getOwnerOf (testCountry.getId ()).is (testPlayer.getId ()));
    }
  }

  @Test
  public void testAssignCountriesToUniqueOwners ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      final Player testPlayer = PlayerFactory.create ("TestPlayer");
      modelTest.requestToAssignCountryOwner (testCountry.getId (), testPlayer.getId ());
      assertTrue (modelTest.getOwnerOf (testCountry.getId ()).is (testPlayer.getId ()));
    }
  }

  @Test
  public void testUnassignCountry ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final Country country = Randomness.getRandomElementFrom (defaultTestCountries);

    modelTest.requestToAssignCountryOwner (country.getId (), testPlayer.getId ());
    modelTest.requestToUnassignCountry (country.getId ());
    assertFalse (modelTest.isCountryAssigned (country.getId ()));
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
  public void testHasAnyUnassignedCountriesAll ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertFalse (modelTest.isCountryAssigned (idOf (testCountry)));
    }
    assertTrue (modelTest.hasUnassignedCountries ());
  }

  @Test
  public void testHasAnyUnassignedCountriesNone ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testOwner = PlayerFactory.create ("TestOwner");

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (idOf (testCountry)));
      assertTrue (modelTest.requestToAssignCountryOwner (idOf (testCountry), idOf (testOwner)).succeeded ());
    }
    assertFalse (modelTest.hasUnassignedCountries ());
  }

  @Test
  public void testHasAnyUnassignedCountriesOne ()
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
      i++;
    }
    assertTrue (modelTest.hasUnassignedCountries ());
  }

  private PlayMapModel createPlayMapModelTestWith (final ImmutableSet <Country> countries)
  {
    final GameRules classicRules = new ClassicGameRules.Builder ().build ();
    return new PlayMapModel (countries, classicRules);
  }
}
