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

import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class SpectatorJoinGameSuccessEvent extends AbstractJoinGameSuccessEvent <SpectatorPacket>
{
  /**
   * Convenience constructor for when person identity is unknown or not cared about, and there are no players in game or
   * players in game are unknown or not cared about.
   */
  public SpectatorJoinGameSuccessEvent (final SpectatorPacket spectator,
                                        final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                        final PersonLimits personLimits)
  {
    super (spectator, ImmutableSet.<PlayerPacket> of (), spectatorsInGame, personLimits);
  }

  /**
   * Convenience constructor for when person identity is unknown or not cared about.
   */
  public SpectatorJoinGameSuccessEvent (final SpectatorPacket spectator,
                                        final ImmutableSet <PlayerPacket> playersInGame,
                                        final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                        final PersonLimits personLimits)
  {
    super (spectator, playersInGame, spectatorsInGame, personLimits);
  }

  public SpectatorJoinGameSuccessEvent (final SpectatorPacket spectator,
                                        final PersonIdentity identity,
                                        final ImmutableSet <PlayerPacket> playersInGame,
                                        final ImmutableSet <SpectatorPacket> spectatorsInGame,
                                        final PersonLimits personLimits)
  {
    super (spectator, identity, playersInGame, spectatorsInGame, personLimits);
  }

  public ImmutableSet <SpectatorPacket> getOtherSpectatorsInGame ()
  {
    return ImmutableSet.copyOf (Sets.filter (getSpectatorsInGame (), new Predicate <SpectatorPacket> ()
    {
      @Override
      public boolean apply (final SpectatorPacket input)
      {
        return input.isNot (getPerson ());
      }
    }));
  }

  @RequiredForNetworkSerialization
  private SpectatorJoinGameSuccessEvent ()
  {
  }
}
