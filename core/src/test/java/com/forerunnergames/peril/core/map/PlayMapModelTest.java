package com.forerunnergames.peril.core.map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

public class PlayMapModelTest
{
  private ImmutableSet <Country> defaultTestCountries;

  public static ImmutableSet <Country> generateTestCountries (final int count)
  {
    Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < 20; i++)
    {
      final Country mockedCountry = Mockito.mock (Country.class);
      final Id mockedCountryId = IdGenerator.generateUniqueId ();
      Mockito.when (mockedCountry.getCountryName ()).thenReturn (new CountryName ("Country-" + i));
      Mockito.when (mockedCountry.getId ()).thenReturn (mockedCountryId);
      Mockito.when (mockedCountry.is (mockedCountry)).thenReturn (true);
      countrySetBuilder.add (mockedCountry);
    }
    return countrySetBuilder.build ();
  }

  @Before
  public void setup ()
  {
    this.defaultTestCountries = generateTestCountries (20);
  }

  @Test
  public void testAssignCountriesToSingleOwner ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");

    for (Country testCountry : defaultTestCountries)
    {
      modelTest.assignCountryOwner (testCountry.getId (), testPlayer.getId ());
      assertTrue (modelTest.getOwnerOf (testCountry.getId ()).is (testPlayer.getId ()));
    }
  }

  @Test
  public void testAssignCountriesToUniqueOwners ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (Country testCountry : defaultTestCountries)
    {
      final Player testPlayer = PlayerFactory.create ("TestPlayer");
      modelTest.assignCountryOwner (testCountry.getId (), testPlayer.getId ());
      assertTrue (modelTest.getOwnerOf (testCountry.getId ()).is (testPlayer.getId ()));
    }
  }

  @Test
  public void testUnassignCountry ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);
    final Player testPlayer = PlayerFactory.create ("TestPlayer");
    final Country country = Randomness.getRandomElementFrom (defaultTestCountries);

    modelTest.assignCountryOwner (country.getId (), testPlayer.getId ());
    modelTest.unassignCountry (country.getId ());
    assertFalse (modelTest.isCountryAssigned (country.getId ()));
  }

  @Test
  public void testCountryWith ()
  {
    final PlayMapModel modelTest = createPlayMapModelTestWith (defaultTestCountries);

    for (Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.countryWith (testCountry.getId ()).is (testCountry));
    }
  }

  private PlayMapModel createPlayMapModelTestWith (final ImmutableSet <Country> countries)
  {
    final GameRules classicRules = new ClassicGameRules.Builder ().build ();
    return new PlayMapModel (countries, classicRules);
  }
}
