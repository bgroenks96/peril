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

package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.common.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Collection;

import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

final class PlayerPackets
{
  static PlayerPacket from (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return new DefaultPlayerPacket (idOf (player).value (), nameOf (player), player.getColor (), player.getTurnOrder ()
            .asInt (), player.getArmiesInHand (), player.getCardsInHand ());
  }

  static ImmutableSet <PlayerPacket> fromPlayers (final Collection <Player> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    final Builder <PlayerPacket> packetSetBuilder = ImmutableSet.builder ();
    for (final Player player : players)
    {
      packetSetBuilder.add (from (player));
    }
    return packetSetBuilder.build ();
  }

  static boolean playerMatchesPacket (final Player player, final PlayerPacket playerPacket)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (playerPacket, "playerPacket");

    return from (player).is (playerPacket);
  }

  private PlayerPackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
