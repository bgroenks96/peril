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

package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalResponseEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public class UpdatePlayerDataResponseEvent extends AbstractInternalResponseEvent
{
  final ImmutableSet <PlayerPacket> updatedPlayers;

  public UpdatePlayerDataResponseEvent (final ImmutableSet <PlayerPacket> updatedPlayers, final Id requestEventId)
  {
    super (requestEventId);

    Arguments.checkIsNotNull (updatedPlayers, "updatedPlayers");
    Arguments.checkIsNotNull (requestEventId, "requestEventId");

    this.updatedPlayers = updatedPlayers;
  }

  public ImmutableSet <PlayerPacket> getUpdatedPlayers ()
  {
    return updatedPlayers;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | UpdatedPlayers: [{}]", super.toString (), updatedPlayers);
  }
}
