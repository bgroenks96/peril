package com.forerunnergames.peril.common.net.packets.battle;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

import com.google.common.collect.ImmutableSortedMap;

import java.util.Comparator;

public interface BattleResultPacket
{
  BattleActorPacket getAttacker ();

  BattleActorPacket getDefender ();

  /**
   * @return the PlayerPacket representing the defending country owner, after the battle has completed; Note: the player
   *         will always be the same as the defending player unless the battle resulted in ownership changing to the
   *         attacker.
   */
  PlayerPacket getDefendingCountryOwner ();

  ImmutableSortedMap <Integer, DieOutcome> getAttackerRollResults ();

  ImmutableSortedMap <Integer, DieOutcome> getDefenderRollResults ();

  /**
   * Network packet version of {@link com.forerunnergames.peril.core.game.DieOutcome}
   */
  public enum DieOutcome
  {
    WIN,
    LOSE,
    NONE;

    public static final Comparator <Integer> DESCENDING = new Comparator <Integer> ()
    {
      @Override
      public int compare (final Integer arg0, final Integer arg1)
      {
        return arg1.compareTo (arg0);
      }
    };
  }
}
