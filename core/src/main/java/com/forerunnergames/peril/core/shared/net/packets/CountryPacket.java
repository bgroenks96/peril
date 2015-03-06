package com.forerunnergames.peril.core.shared.net.packets;

import com.forerunnergames.peril.core.model.map.country.CountryName;

public interface CountryPacket
{
  public CountryName getCountryName ();

  public int getArmyCount ();

  public boolean hasAnyArmies ();

  public boolean hasAtLeastNArmies (final int n);
}
