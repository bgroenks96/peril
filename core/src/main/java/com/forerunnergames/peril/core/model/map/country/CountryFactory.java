package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

public final class CountryFactory
{
  public static CountryBuilder builder (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return new CountryBuilder (name);
  }

  public static Country create (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return builder (name).build ();
  }

  public static Country create (final String name, final int armyCount)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    return builder (name).armies (armyCount).build ();
  }

  public static class CountryBuilder
  {
    private final String name;
    private final Id id;
    private int armyCount;

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
}
