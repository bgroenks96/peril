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

package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class DefaultPendingBattleActorPacket implements PendingBattleActorPacket
{
  private final PlayerPacket player;
  private final CountryPacket country;
  private final DieRange dieRange;

  public DefaultPendingBattleActorPacket (final PlayerPacket player,
                                          final CountryPacket country,
                                          final DieRange dieRange)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkIsNotNull (dieRange, "dieRange");

    this.player = player;
    this.country = country;
    this.dieRange = dieRange;
  }

  @Override
  public final PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public final String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public final CountryPacket getCountry ()
  {
    return country;
  }

  @Override
  public final int getCountryArmyCount ()
  {
    return country.getArmyCount ();
  }

  @Override
  public final String getCountryName ()
  {
    return country.getName ();
  }

  @Override
  public DieRange getDieRange ()
  {
    return dieRange;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | Country: [{}] | DieRange: [{}]", getClass ().getSimpleName (), player,
                           country, dieRange);
  }

  @RequiredForNetworkSerialization
  DefaultPendingBattleActorPacket ()
  {
    player = null;
    country = null;
    dieRange = null;
  }
}
