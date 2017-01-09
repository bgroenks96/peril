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
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerQuitGameSuccessEvent extends AbstractPlayerEvent implements PlayerSuccessEvent
{
  private final ImmutableSet <PlayerPacket> remainingPlayersInGame;
  private final ImmutableSet <PlayerPacket> disconnectedPlayers;

  public PlayerQuitGameSuccessEvent (final PlayerPacket player,
                                     final ImmutableSet <PlayerPacket> remainingPlayersInGame,
                                     final ImmutableSet <PlayerPacket> disconnectedPlayers)
  {
    super (player);

    this.remainingPlayersInGame = remainingPlayersInGame;
    this.disconnectedPlayers = disconnectedPlayers;
  }

  public ImmutableSet <PlayerPacket> getRemainingPlayersInGame ()
  {
    return remainingPlayersInGame;
  }

  public ImmutableSet <PlayerPacket> getDisconnectedPlayers ()
  {
    return disconnectedPlayers;
  }

  @RequiredForNetworkSerialization
  private PlayerQuitGameSuccessEvent ()
  {
    remainingPlayersInGame = null;
    disconnectedPlayers = null;
  }
}
