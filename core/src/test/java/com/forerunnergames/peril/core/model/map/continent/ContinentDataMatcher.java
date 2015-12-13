package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class ContinentDataMatcher
{
  private final ImmutableSet <ContinentData> continentData;

  public ContinentDataMatcher (final ImmutableSet <ContinentData> continentData)
  {
    Arguments.checkIsNotNull (continentData, "continentData");

    this.continentData = continentData;
  }

  public boolean continentDataMatch (final ContinentFactory factory)
  {
    Arguments.checkIsNotNull (factory, "factory");

    final ImmutableSet <Continent> continents = factory.getContinents ();

    if (continents.size () != continentData.size ()) return false;

    for (final Continent continent : continents)
    {
      boolean isMatch = false;
      for (final ContinentData matchData : continentData)
      {
        if (!matchData.name.equals (continent.getName ())) continue;
        if (matchData.reinforcementBonus != continent.getReinforcementBonus ()) continue;
        if (matchData.countryCount != continent.getCountryCount ()) continue;
        isMatch = true;
        break;
      }
      if (isMatch) return true;
    }

    return false;
  }

  public static class ContinentData
  {
    private final String name;
    private final int reinforcementBonus;
    private final int countryCount;

    public ContinentData (final String name, final int reinforcementBonus, final int countryCount)
    {
      Arguments.checkIsNotNull (name, "name");
      Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
      Arguments.checkIsNotNegative (countryCount, "countryCount");

      this.name = name;
      this.reinforcementBonus = reinforcementBonus;
      this.countryCount = countryCount;
    }
  }
}
