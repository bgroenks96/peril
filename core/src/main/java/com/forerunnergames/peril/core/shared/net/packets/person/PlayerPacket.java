package com.forerunnergames.peril.core.shared.net.packets.person;


public interface PlayerPacket extends PersonPacket
{
  String getColor ();

  int getTurnOrder ();

  int getArmiesInHand ();

  boolean has (final String color);

  boolean has (final int turnOrder);

  boolean hasArmiesInHand (final int armies);

  boolean doesNotHave (final String color);

  boolean doesNotHave (final int turnOrder);
}
