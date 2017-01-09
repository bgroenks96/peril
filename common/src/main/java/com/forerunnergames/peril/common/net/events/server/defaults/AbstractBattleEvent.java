/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleEvent;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractBattleEvent extends AbstractPlayerEvent implements BattleEvent
{
  private final PendingBattleActorPacket attacker;
  private final PendingBattleActorPacket defender;

  /**
   * @param primaryPlayer
   *          Attacking player for attack events, defending player for defend events.
   */
  protected AbstractBattleEvent (final PlayerPacket primaryPlayer,
                                 final PendingBattleActorPacket attacker,
                                 final PendingBattleActorPacket defender)
  {
    super (primaryPlayer);

    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");

    this.attacker = attacker;
    this.defender = defender;
  }

  @RequiredForNetworkSerialization
  protected AbstractBattleEvent ()
  {
    attacker = null;
    defender = null;
  }

  @Override
  public PendingBattleActorPacket getAttacker ()
  {
    return attacker;
  }

  @Override
  public PendingBattleActorPacket getDefender ()
  {
    return defender;
  }

  @Override
  public PlayerPacket getAttackingPlayer ()
  {
    return attacker.getPlayer ();
  }

  @Override
  public PlayerPacket getDefendingPlayer ()
  {
    return defender.getPlayer ();
  }

  @Override
  public String getAttackingPlayerName ()
  {
    return attacker.getPlayerName ();
  }

  @Override
  public String getDefendingPlayerName ()
  {
    return defender.getPlayerName ();
  }

  @Override
  public PlayerColor getAttackingPlayerColor ()
  {
    return attacker.getPlayerColor ();
  }

  @Override
  public PlayerColor getDefendingPlayerColor ()
  {
    return defender.getPlayerColor ();
  }

  @Override
  public int getAttackingPlayerTurnOrder ()
  {
    return attacker.getPlayerTurnOrder ();
  }

  @Override
  public int getDefendingPlayerTurnOrder ()
  {
    return defender.getPlayerTurnOrder ();
  }

  @Override
  public int getAttackingPlayerArmiesInHand ()
  {
    return attacker.getPlayerArmiesInHand ();
  }

  @Override
  public int getDefendingPlayerArmiesInHand ()
  {
    return defender.getPlayerArmiesInHand ();
  }

  @Override
  public int getAttackingPlayerCardsInHand ()
  {
    return attacker.getPlayerCardsInHand ();
  }

  @Override
  public int getDefendingPlayerCardsInHand ()
  {
    return defender.getPlayerCardsInHand ();
  }

  @Override
  public DieRange getAttackerDieRange ()
  {
    return attacker.getDieRange ();
  }

  @Override
  public DieRange getDefenderDieRange ()
  {
    return defender.getDieRange ();
  }

  @Override
  public CountryPacket getAttackingCountry ()
  {
    return attacker.getCountry ();
  }

  @Override
  public CountryPacket getDefendingCountry ()
  {
    return defender.getCountry ();
  }

  @Override
  public String getAttackingCountryName ()
  {
    return attacker.getCountryName ();
  }

  @Override
  public String getDefendingCountryName ()
  {
    return defender.getCountryName ();
  }

  @Override
  public int getAttackingCountryArmyCount ()
  {
    return attacker.getCountryArmyCount ();
  }

  @Override
  public int getDefendingCountryArmyCount ()
  {
    return defender.getCountryArmyCount ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Attacker: [{}] | Defender: [{}]", super.toString (), attacker, defender);
  }
}
