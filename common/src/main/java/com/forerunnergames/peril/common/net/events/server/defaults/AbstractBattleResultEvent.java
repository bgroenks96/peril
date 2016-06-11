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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.BattleResultEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractBattleResultEvent implements BattleResultEvent
{
  private final BattleResultPacket result;

  protected AbstractBattleResultEvent (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    this.result = result;
  }

  @Override
  public BattleResultPacket getBattleResult ()
  {
    return result;
  }

  @Override
  public PlayerPacket getAttackingPlayer ()
  {
    return result.getAttacker ().getPlayer ();
  }

  @Override
  public String getAttackingPlayerName ()
  {
    return result.getAttacker ().getPlayerName ();
  }

  @Override
  public int getAttackerDieCount ()
  {
    return result.getAttacker ().getDieCount ();
  }

  @Override
  public PlayerPacket getDefendingPlayer ()
  {
    return result.getDefender ().getPlayer ();
  }

  @Override
  public String getDefendingPlayerName ()
  {
    return result.getDefender ().getPlayerName ();
  }

  @Override
  public int getDefenderDieCount ()
  {
    return result.getDefender ().getDieCount ();
  }

  @Override
  public CountryPacket getAttackingCountry ()
  {
    return result.getAttacker ().getCountry ();
  }

  @Override
  public int getAttackingCountryArmyCount ()
  {
    return result.getAttacker ().getCountryArmyCount ();
  }

  @Override
  public int getAttackingCountryArmyDelta ()
  {
    return result.getAttackingCountryArmyDelta ();
  }

  @Override
  public int getDefendingCountryArmyDelta ()
  {
    return result.getDefendingCountryArmyDelta ();
  }

  @Override
  public String getAttackingCountryName ()
  {
    return result.getAttacker ().getCountryName ();
  }

  @Override
  public String getDefendingCountryName ()
  {
    return result.getDefender ().getCountryName ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Result: [{}]", getClass ().getSimpleName (), result);
  }

  @RequiredForNetworkSerialization
  protected AbstractBattleResultEvent ()
  {
    result = null;
  }
}
