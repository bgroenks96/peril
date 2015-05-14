package com.forerunnergames.peril.core.shared.net.packets;


public interface CountryPacket
{
  String getCountryName ();

  int getArmyCount ();

  boolean hasAnyArmies ();

  boolean hasAtLeastNArmies (final int n);
}
