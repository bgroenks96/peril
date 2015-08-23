package com.forerunnergames.peril.common.net.packets.territory;

public interface CountryPacket extends TerritoryPacket
{
  int getArmyCount ();

  boolean hasAnyArmies ();

  boolean hasAtLeastNArmies (final int n);
}
