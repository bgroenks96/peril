package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public class DefaultContinent extends AbstractAsset implements Continent
{
  private final ContinentName name;
  private final ImmutableSet <Id> countryIds;
  private final int reinforcementBonus;

  public DefaultContinent (final String name,
                           final Id id,
                           final int reinforcementBonus,
                           final ImmutableSet <Id> countryIds)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
    Arguments.checkIsNotNull (countryIds, "countryIds");
    Arguments.checkHasNoNullElements (countryIds, "countryIds");

    this.name = new ContinentName (name);
    this.countryIds = countryIds;
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
    return countryIds;
  }

  @Override
  public int getCountryCount ()
  {
    return countryIds.size ();
  }

  @Override
  public boolean hasCountry (final Id country)
  {
    Arguments.checkIsNotNull (country, "country");

    return countryIds.contains (country);
  }

  @Override
  public int getReinforcementBonus ()
  {
    return reinforcementBonus;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | %2$s | Reinforcement Bonus: %3$s | Country Count: %4$s | Country Id's: %5$s",
                          super.toString (), name, reinforcementBonus, countryIds.size (), countryIds);
  }

  @RequiredForNetworkSerialization
  private DefaultContinent ()
  {
    name = null;
    countryIds = null;
    reinforcementBonus = 0;
  }
}
