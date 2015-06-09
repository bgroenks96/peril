package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public class DefaultContinent extends AbstractAsset implements Continent
{
  private final ContinentName name;
  private final ImmutableSet <Id> countries;
  private final int reinforcementBonus;

  public DefaultContinent (final String name,
                           final Id id,
                           final int reinforcementBonus,
                           final ImmutableSet <Id> countries)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

    this.name = new ContinentName (name);
    this.countries = countries;
    this.reinforcementBonus = reinforcementBonus;
  }

  @Override
  public ContinentName getContinentName ()
  {
    return name;
  }

  @Override
  public ImmutableSet <Id> getCountryIds ()
  {
    return countries;
  }

  @Override
  public boolean hasCountry (final Id country)
  {
    Arguments.checkIsNotNull (country, "country");

    return countries.contains (country);
  }

  @Override
  public int getReinforcementBonus ()
  {
    return reinforcementBonus;
  }

  @RequiredForNetworkSerialization
  private DefaultContinent ()
  {
    name = null;
    countries = null;
    reinforcementBonus = 0;
  }
}
