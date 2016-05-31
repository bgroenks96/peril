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

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class PlayerJoinGameSuccessEvent implements PlayerSuccessEvent
{
  private final PlayerPacket player;
  private final ImmutableSet <PlayerPacket> playersInGame;

  public PlayerJoinGameSuccessEvent (final PlayerPacket player, final ImmutableSet <PlayerPacket> playersInGame)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");

    this.player = player;
    this.playersInGame = playersInGame;
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
  public PlayerColor getPlayerColor ()
  {
    return player.getColor ();
  }

  public int getPlayerTurnOrder ()
  {
    return player.getTurnOrder ();
  }

  public ImmutableSet <PlayerPacket> getPlayersInGame ()
  {
    return playersInGame;
  }

  public ImmutableSet <PlayerPacket> getOtherPlayersInGame ()
  {
    return ImmutableSet.copyOf (Sets.filter (playersInGame, new Predicate <PlayerPacket> ()
    {
      @Override
      public boolean apply (final PlayerPacket input)
      {
        return input.isNot (player);
      }
    }));
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: {}", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameSuccessEvent ()
  {
    player = null;
    playersInGame = null;
  }
}
