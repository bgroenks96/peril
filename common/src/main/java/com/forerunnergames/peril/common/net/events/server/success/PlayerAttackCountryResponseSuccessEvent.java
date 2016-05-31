/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableList;

public final class PlayerAttackCountryResponseSuccessEvent implements PlayerResponseSuccessEvent
{
  private final BattleResultPacket result;

  public PlayerAttackCountryResponseSuccessEvent (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    this.result = result;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return getAttackingPlayer ();
  }

  @Override
  public String getPlayerName ()
  {
    return getPlayer ().getName ();
  }

  @Override
  public PlayerColor getPlayerColor ()
  {
    return getPlayer ().getColor ();
  }

  public BattleResultPacket getBattleResult ()
  {
    return result;
  }

  public PlayerPacket getAttackingPlayer ()
  {
    return result.getAttacker ().getPlayer ();
  }

  public String getAttackingPlayerName ()
  {
    return result.getAttacker ().getPlayerName ();
  }

  public int getAttackerDieCount ()
  {
    return result.getAttacker ().getDieCount ();
  }

  public PlayerPacket getDefendingPlayer ()
  {
    return result.getDefender ().getPlayer ();
  }

  public String getDefendingPlayerName ()
  {
    return result.getDefender ().getPlayerName ();
  }

  public int getDefenderDieCount ()
  {
    return result.getDefender ().getDieCount ();
  }

  public CountryPacket getAttackingCountry ()
  {
    return result.getAttacker ().getCountry ();
  }

  public int getAttackingCountryArmyCount ()
  {
    return result.getAttacker ().getCountryArmyCount ();
  }

  public int getAttackingCountryArmyDelta ()
  {
    return result.getAttackingCountryArmyDelta ();
  }

  public String getAttackingCountryName ()
  {
    return result.getAttacker ().getCountryName ();
  }

  public CountryPacket getDefendingCountry ()
  {
    return result.getDefender ().getCountry ();
  }

  public String getDefendingCountryName ()
  {
    return result.getDefender ().getCountryName ();
  }

  public int getDefendingCountryArmyCount ()
  {
    return result.getDefender ().getCountryArmyCount ();
  }

  public int getDefendingCountryArmyDelta ()
  {
    return result.getDefendingCountryArmyDelta ();
  }

  public ImmutableList <DieRoll> getAttackerRolls ()
  {
    return result.getAttackerRolls ();
  }

  public ImmutableList <DieRoll> getDefenderRolls ()
  {
    return result.getDefenderRolls ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Result: [{}]", getClass ().getSimpleName (), result);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryResponseSuccessEvent ()
  {
    result = null;
  }
}
