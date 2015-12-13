package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public final class ContinentFactory
{
  private final ImmutableSet.Builder <Continent> continents = ImmutableSet.builder ();
  private int continentCount = 0;

  public void newContinentWith (final String name, final ImmutableSet <Id> countries)
  {
    continents.add (create (name, countries));
    continentCount++;
  }

  public void newContinentWith (final String name, final int reinforcementBonus, final ImmutableSet <Id> countries)
  {
    continents.add (create (name, reinforcementBonus, countries));
    continentCount++;
  }

  public int getContinentCount ()
  {
    return continentCount;
  }

  ImmutableSet <Continent> getContinents ()
  {
    return continents.build ();
  }

  static ContinentBuilder builder (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return new ContinentBuilder (name);
  }

  static Continent create (final String name, final ImmutableSet <Id> countries)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

    return builder (name).countries (countries).build ();
  }

  static Continent create (final String name, final int reinforcementBonus, final ImmutableSet <Id> countries)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

    return builder (name).reinforcementBonus (reinforcementBonus).countries (countries).build ();
  }

  @Override
  public String toString ()
  {
    return continents.build ().toString ();
  }

  static class ContinentBuilder
  {
    private final String continentName;
    private final Id id;

    private int reinforcementBonus;
    private final Set <Id> countries = new HashSet <> ();

    public ContinentBuilder (final String continentName)
    {
      Arguments.checkIsNotNull (continentName, "continentName");

      this.continentName = continentName;
      id = IdGenerator.generateUniqueId ();
    }

    public ContinentBuilder reinforcementBonus (final int reinforcementBonus)
    {
      Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");

      this.reinforcementBonus = reinforcementBonus;
      return this;
    }

    public ContinentBuilder countries (final ImmutableSet <Id> countries)
    {
      Arguments.checkIsNotNull (countries, "countries");
      Arguments.checkHasNoNullElements (countries, "countries");

      this.countries.addAll (countries);
      return this;
    }

    public ContinentBuilder country (final Id country)
    {
      Arguments.checkIsNotNull (country, "country");

      countries.add (country);
      return this;
    }

    public Continent build ()
    {
      return new DefaultContinent (continentName, id, reinforcementBonus, ImmutableSet.copyOf (countries));
    }
  }
}
