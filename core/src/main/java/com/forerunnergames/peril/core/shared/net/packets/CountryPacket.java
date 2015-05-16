package com.forerunnergames.peril.core.shared.net.packets;

public interface CountryPacket extends GamePacket
{
  String getCountryName ();

  int getArmyCount ();

  boolean has (final String name);

  boolean hasAnyArmies ();

  boolean hasAtLeastNArmies (final int n);
}
