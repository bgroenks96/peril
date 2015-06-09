package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.shared.net.packets.territory.AbstractTerritoryPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public class DefaultContinentPacket extends AbstractTerritoryPacket implements ContinentPacket
{
  private final ImmutableSet <CountryPacket> countries;
  private final int reinforcementBonus;

  public DefaultContinentPacket (final String name,
                                 final int id,
                                 final int reinforcementBonus,
                                 final Collection <CountryPacket> countries)
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

  @RequiredForNetworkSerialization
  private DefaultContinentPacket ()
  {
    super (null, 0);

    countries = null;
    reinforcementBonus = 0;
  }

}
