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

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import javax.annotation.Nullable;

public final class PlayerClaimCountryResponseSuccessEvent extends AbstractPlayerCountryArmiesChangedEvent implements
        PlayerResponseSuccessEvent, CountryOwnerChangedEvent
{
  public PlayerClaimCountryResponseSuccessEvent (final PlayerPacket player,
                                                 final CountryPacket claimedCountry,
                                                 final int deltaArmyCount)
  {
    super (player, claimedCountry, -deltaArmyCount, deltaArmyCount);
  }

  @Override
  public boolean hasPreviousOwner ()
  {
    return false;
  }

  @Override
  @Nullable
  public PlayerPacket getPreviousOwner ()
  {
    return null;
  }

  @Override
  public PlayerPacket getNewOwner ()
  {
    return getPlayer ();
  }

  @Override
  public String getNewOwnerName ()
  {
    return getPlayerName ();
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryResponseSuccessEvent ()
  {
  }
}
