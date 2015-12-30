package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultBattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Classes;

public final class BattlePackets
{
  public static BattleActorPacket from (final BattleActor actor,
                                        final PlayerModel playerModel,
                                        final CountryMapGraphModel mapGraphModel)
  {
    final PlayerPacket player = playerModel.playerPacketWith (actor.getPlayerId ());
    final CountryPacket country = mapGraphModel.countryPacketWith (actor.getCountryId ());
    return new DefaultBattleActorPacket (player, country, actor.getDieCount ());
  }

  public static BattleResultPacket from (final BattleResult result,
                                         final PlayerModel playerModel,
                                         final CountryMapGraphModel mapGraphModel)
  {
    final BattleActorPacket attacker = from (result.getAttacker (), playerModel, mapGraphModel);
    final BattleActorPacket defender = from (result.getDefender (), playerModel, mapGraphModel);
    return new DefaultBattleResultPacket (attacker, defender,
            playerModel.playerPacketWith (result.getDefendingCountryOwner ()), result.getAttackerRollResults (),
            result.getDefenderRollResults ());
  }

  private BattlePackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
