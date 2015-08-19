package com.forerunnergames.peril.core.model.io;

import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.io.CountryModelDataLoader;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.tools.common.id.Id;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import org.junit.BeforeClass;

public class CountryModelDataLoaderTest extends DataLoaderTest <Id, Country>
{
  private static final String TEST_COUNTRIES_FILENAME = "test-countries.txt";
  private static final int EXPECTED_COUNTRY_COUNT_FROM_FILE = 10;
  private static Collection <Matcher <? super Country>> countryMatchers = new ArrayList <> ();

  @BeforeClass
  public static void setupClass ()
  {
    for (int i = 1; i <= EXPECTED_COUNTRY_COUNT_FROM_FILE; ++i)
    {
      final Country expectedCountry = mock (Country.class);
      final String expectedCountryName = "Test Country " + i;

      when (expectedCountry.getName ()).thenReturn (expectedCountryName);

      countryMatchers.add (hasName (equalTo (expectedCountryName)));
    }
  }

  @Override
  public DataLoader <Id, Country> createDataLoader ()
  {
    return new CountryModelDataLoader (new InternalStreamParserFactory ());
  }

  @Override
  public Collection <Matcher <? super Country>> getDataMatchers ()
  {
    return countryMatchers;
  }

  @Override
  public String getTestDataFileName ()
  {
    return TEST_COUNTRIES_FILENAME;
  }

  private static Matcher <Country> hasName (final Matcher <String> nameMatcher)
  {
    return new FeatureMatcher <Country, String> (nameMatcher, "Country with name", "name")
    {
      @Override
      protected String featureValueOf (final Country actual)
      {
        return actual.getName ();
      }
    };
  }
}
