package com.forerunnergames.peril.core.shared.net.packets;

import com.forerunnergames.peril.core.model.map.country.CountryName;

public interface CountryPacket
{
  CountryName getCountryName ();

  int getArmyCount ();

  boolean hasAnyArmies ();

  boolean hasAtLeastNArmies (final int n);
}
