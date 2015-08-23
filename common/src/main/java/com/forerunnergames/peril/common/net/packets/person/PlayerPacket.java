package com.forerunnergames.peril.common.net.packets.person;

import java.util.Comparator;

public interface PlayerPacket extends PersonPacket
{
  Comparator <PlayerPacket> TURN_ORDER_COMPARATOR = new Comparator <PlayerPacket> ()
  {
    @Override
    public int compare (final PlayerPacket o1, final PlayerPacket o2)
    {
      if (o1.getTurnOrder () < o2.getTurnOrder ())
      {
        return -1;
      }
      else if (o1.getTurnOrder () > o2.getTurnOrder ())
      {
        return 1;
      }
      else
      {
        return 0;
      }
    }
  };

  String getColor ();

  int getTurnOrder ();

  int getArmiesInHand ();

  boolean has (final String color);

  boolean has (final int turnOrder);

  boolean hasArmiesInHand (final int armies);

  boolean doesNotHave (final String color);

  boolean doesNotHave (final int turnOrder);
}
