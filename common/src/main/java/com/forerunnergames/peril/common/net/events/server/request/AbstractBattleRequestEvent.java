/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleRequestEvent;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

abstract class AbstractBattleRequestEvent extends AbstractPlayerEvent implements BattleRequestEvent
{
  private final PendingBattleActorPacket attacker;
  private final PendingBattleActorPacket defender;

  AbstractBattleRequestEvent (final PendingBattleActorPacket attacker, final PendingBattleActorPacket defender)
  {
    super (attacker.getPlayer ());

    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");

    this.attacker = attacker;
    this.defender = defender;
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
  public boolean playersAndCountriesMatch (final BattleRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return attacker.playerAndCountryMatches (event.getAttacker ())
            && defender.playerAndCountryMatches (event.getDefender ());
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Attacker: [{}] | Defender: [{}]", super.toString (), attacker, defender);
  }

  @RequiredForNetworkSerialization
  protected AbstractBattleRequestEvent ()
  {
    attacker = null;
    defender = null;
  }
}
