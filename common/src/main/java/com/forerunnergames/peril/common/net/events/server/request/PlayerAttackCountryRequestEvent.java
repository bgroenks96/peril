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

package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMultimap;

public final class PlayerAttackCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors;

  public PlayerAttackCountryRequestEvent (final PlayerPacket currentPlayer,
                                          final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors)
  {
    super (currentPlayer);

    Arguments.checkIsNotNull (validAttackVectors, "validAttackVectors");

    this.validAttackVectors = validAttackVectors;
  }

  public ImmutableMultimap <CountryPacket, CountryPacket> getValidAttackVectors ()
  {
    return validAttackVectors;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | AttackVectors: [{}]", super.toString (), validAttackVectors);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryRequestEvent ()
  {
    validAttackVectors = null;
  }
}
