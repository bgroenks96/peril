package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.common.net.packets.defaults.DefaultContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;

final class ContinentPackets
{
  static ContinentPacket from (final Continent continent, final ImmutableSet <CountryPacket> countries)
  {
    Arguments.checkIsNotNull (continent, "country");

    return new DefaultContinentPacket (continent.getName (), continent.getId ().value (),
            continent.getReinforcementBonus (), countries);
  }

  private ContinentPackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
