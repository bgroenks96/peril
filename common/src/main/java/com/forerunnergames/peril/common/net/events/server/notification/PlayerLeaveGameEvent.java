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

package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerLeaveGameEvent implements PlayerNotificationEvent
{
  private final PlayerPacket player;
  private final ImmutableSet <PlayerPacket> playersLeftInGame;

  public PlayerLeaveGameEvent (final PlayerPacket player, final ImmutableSet <PlayerPacket> playersLeftInGame)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
    this.playersLeftInGame = playersLeftInGame;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public String getPlayerColor ()
  {
    return player.getColor ();
  }

  public ImmutableSet <PlayerPacket> getPlayersLeftInGame ()
  {
    return playersLeftInGame;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | RemainingPlayers: [{}]", getClass ().getSimpleName (), player,
                           playersLeftInGame);
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameEvent ()
  {
    player = null;
    playersLeftInGame = null;
  }
}
