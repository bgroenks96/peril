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

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.Objects;

import javax.annotation.Nullable;

public final class DefaultCountryOwnerChangedEvent extends AbstractCountryEvent implements CountryOwnerChangedEvent
{
  @Nullable
  private final PlayerPacket newOwner;
  @Nullable
  private final PlayerPacket previousOwner;

  public DefaultCountryOwnerChangedEvent (final CountryPacket country, @Nullable final PlayerPacket newOwner)
  {
    this (country, newOwner, null);
  }

  public DefaultCountryOwnerChangedEvent (final CountryPacket country,
                                          @Nullable final PlayerPacket newOwner,
                                          @Nullable final PlayerPacket previousOwner)
  {
    super (country);

    Arguments
            .checkIsFalse (Objects.equals (previousOwner, newOwner),
                           "previousOwner [{}] and newOwner [{}] cannot be the same (otherwise no ownership change occurred).");

    this.newOwner = newOwner;
    this.previousOwner = previousOwner;
  }

  @Override
  public boolean hasPreviousOwner ()
  {
    return previousOwner != null;
  }

  @Nullable
  @Override
  public PlayerPacket getPreviousOwner ()
  {
    return previousOwner;
  }

  @Override
  public String getPreviousOwnerName ()
  {
    return previousOwner != null ? previousOwner.getName () : "";
  }

  @Override
  public PlayerColor getPreviousOwnerColor ()
  {
    return previousOwner != null ? previousOwner.getColor () : PlayerColor.UNKNOWN;
  }

  @Override
  public boolean hasNewOwner ()
  {
    return newOwner != null;
  }

  @Nullable
  @Override
  public PlayerPacket getNewOwner ()
  {
    return newOwner;
  }

  @Override
  public PlayerColor getNewOwnerColor ()
  {
    return newOwner != null ? newOwner.getColor () : PlayerColor.UNKNOWN;
  }

  @Override
  public String getNewOwnerName ()
  {
    return newOwner != null ? newOwner.getName () : "";
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | NewOwner: [{}] | PreviousOwner: [{}]", super.toString (), newOwner, previousOwner);
  }

  @RequiredForNetworkSerialization
  private DefaultCountryOwnerChangedEvent ()
  {
    newOwner = null;
    previousOwner = null;
  }
}
