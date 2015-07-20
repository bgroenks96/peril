package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.shared.net.packets.territory.AbstractTerritoryPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

import java.util.UUID;

public class DefaultContinentPacket extends AbstractTerritoryPacket implements ContinentPacket
{
  private final ImmutableSet <CountryPacket> countries;
  private final int reinforcementBonus;

  public DefaultContinentPacket (final String name,
                                 final UUID id,
                                 final int reinforcementBonus,
                                 final ImmutableSet <CountryPacket> countries)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

    this.countries = ImmutableSet.copyOf (countries);
    this.reinforcementBonus = reinforcementBonus;
  }

  @Override
  public ImmutableSet <CountryPacket> getCountries ()
  {
    return countries;
  }

  @Override
  public boolean hasCountry (final CountryPacket country)
  {
    Arguments.checkIsNotNull (country, "country");

    return countries.contains (country);
  }

  @Override
  public int getReinforcementBonus ()
  {
    return reinforcementBonus;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | Reinforcement Bonus: %2$s | Countries: %3$s", super.toString (), reinforcementBonus,
                          countries);
  }

  @RequiredForNetworkSerialization
  private DefaultContinentPacket ()
  {
    super (null, null);

    countries = null;
    reinforcementBonus = 0;
  }

}
