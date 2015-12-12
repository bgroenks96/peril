package com.forerunnergames.peril.core.model.people.player;

import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

import com.forerunnergames.peril.common.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Collection;

final class PlayerPackets
{
  static PlayerPacket from (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return new DefaultPlayerPacket (idOf (player).value (), nameOf (player), player.getColor ().toString (),
            player.getTurnOrder ().asInt (), player.getArmiesInHand ());
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
