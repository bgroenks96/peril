package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;

public final class CountryFactory
{
  private final ImmutableSet.Builder <Country> countries = ImmutableSet.builder ();

  private int countryCount = 0;

  public void newCountryWith (final String name)
  {
    countries.add (create (name));
    countryCount++;
  }

  public void newCountryWith (final String name, final int armyCount)
  {
    countries.add (create (name, armyCount));
    countryCount++;
  }

  public int getCountryCount ()
  {
    return countryCount;
  }

  ImmutableSet <Country> getCountries ()
  {
    return countries.build ();
  }

  static CountryBuilder builder (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return new CountryBuilder (name);
  }

  static Country create (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return builder (name).build ();
  }

  static Country create (final String name, final int armyCount)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    return builder (name).armies (armyCount).build ();
  }

  static class CountryBuilder
  {
    private final String name;
    private final Id id;
    private int armyCount = 0;

    public CountryBuilder (final String countryName)
    {
      Arguments.checkIsNotNull (countryName, "countryName");

      name = countryName;
      id = IdGenerator.generateUniqueId ();
    }

    public CountryBuilder armies (final int armyCount)
    {
      Arguments.checkIsNotNegative (armyCount, "armyCount");

      this.armyCount = armyCount;
      return this;
    }

    public Country build ()
    {
      return new DefaultCountry (name, id, armyCount);
    }
  }

  public static CountryFactory generateDefaultCountries (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    final CountryFactory countryFactory = new CountryFactory ();
    for (int i = 0; i < count; ++i)
    {
      countryFactory.newCountryWith ("Country-" + i);
    }
    return countryFactory;
  }
}
