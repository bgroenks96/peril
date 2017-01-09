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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerRetreatSuccessEvent extends AbstractPlayerEvent implements PlayerSuccessEvent
{
  private final PlayerPacket defendingPlayer;
  private final CountryPacket attackingCountry;
  private final CountryPacket defendingCountry;

  public PlayerRetreatSuccessEvent (final PlayerPacket attackingPlayer,
                                    final PlayerPacket defendingPlayer,
                                    final CountryPacket attackingCountry,
                                    final CountryPacket defendingCountry)
  {
    super (attackingPlayer);

    Arguments.checkIsNotNull (defendingPlayer, "defendingPlayer");
    Arguments.checkIsNotNull (attackingCountry, "attackingCountry");
    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");

    this.defendingPlayer = defendingPlayer;
    this.attackingCountry = attackingCountry;
    this.defendingCountry = defendingCountry;
  }

  public PlayerPacket getAttackingPlayer ()
  {
    return getPerson ();
  }

  public String getAttackingPlayerName ()
  {
    return getPersonName ();
  }

  public PlayerPacket getDefendingPlayer ()
  {
    return defendingPlayer;
  }

  public String getDefendingPlayerName ()
  {
    return defendingPlayer.getName ();
  }

  public CountryPacket getAttackingCountry ()
  {
    return attackingCountry;
  }

  public String getAttackingCountryName ()
  {
    return attackingCountry.getName ();
  }

  public int getAttackingCountryArmyCount ()
  {
    return attackingCountry.getArmyCount ();
  }

  public CountryPacket getDefendingCountry ()
  {
    return getDefendingCountry ();
  }

  public String getDefendingCountryName ()
  {
    return defendingCountry.getName ();
  }

  public int getDefendingCountryArmyCount ()
  {
    return defendingCountry.getArmyCount ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DefendingPlayer: {} | AttackingCountry: {} | DefendingCountry: {}", super.toString (),
                           defendingPlayer, attackingCountry, defendingCountry);
  }

  @RequiredForNetworkSerialization
  private PlayerRetreatSuccessEvent ()
  {
    defendingPlayer = null;
    attackingCountry = null;
    defendingCountry = null;
  }
}
