package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultBattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSortedMap;

import java.util.Comparator;

public final class BattlePackets
{
  static final Comparator <Integer> DESCENDING = BattleResultPacket.DieOutcome.DESCENDING;

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
    final ImmutableSortedMap <DieFaceValue, DieOutcome> attackerResults = result.getAttackerRollResults ();
    final ImmutableSortedMap <DieFaceValue, DieOutcome> defenderResults = result.getDefenderRollResults ();
    final ImmutableSortedMap.Builder <Integer, BattleResultPacket.DieOutcome> attackerResultsBuilder = ImmutableSortedMap
            .orderedBy (DESCENDING);
    for (final DieFaceValue dieValue : attackerResults.keySet ())
    {
      attackerResultsBuilder.put (dieValue.value (), toPacketDieOutcome (attackerResults.get (dieValue)));
    }
    final ImmutableSortedMap.Builder <Integer, BattleResultPacket.DieOutcome> defenderResultsBuilder = ImmutableSortedMap
            .orderedBy (DESCENDING);
    for (final DieFaceValue dieValue : defenderResults.keySet ())
    {
      defenderResultsBuilder.put (dieValue.value (), toPacketDieOutcome (defenderResults.get (dieValue)));
    }
    final BattleActorPacket attacker = from (result.getAttacker (), playerModel, mapGraphModel);
    final BattleActorPacket defender = from (result.getDefender (), playerModel, mapGraphModel);
    return new DefaultBattleResultPacket (attacker, defender,
            playerModel.playerPacketWith (result.getDefendingCountryOwner ()), attackerResultsBuilder.build (),
            defenderResultsBuilder.build ());
  }

  private static BattleResultPacket.DieOutcome toPacketDieOutcome (final DieOutcome dieOutcome)
  {
    switch (dieOutcome)
    {
      case WIN:
        return BattleResultPacket.DieOutcome.WIN;
      case LOSE:
        return BattleResultPacket.DieOutcome.LOSE;
      case NONE:
        return BattleResultPacket.DieOutcome.NONE;
      default:
        throw new IllegalArgumentException (Strings.format ("Invalid die outcome value: {}.", dieOutcome));
    }
  }

  private BattlePackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
