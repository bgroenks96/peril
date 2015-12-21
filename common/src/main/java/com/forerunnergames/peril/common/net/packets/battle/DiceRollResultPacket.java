package com.forerunnergames.peril.common.net.packets.battle;

import com.google.common.collect.ImmutableList;

public interface DiceRollResultPacket
{
  /**
   * @return the number of dice rolled
   */
  int getDieCount ();

  /**
   * @return a list of valid die values (1-6) sorted in descending order
   */
  ImmutableList <Integer> getDieValues ();
}
