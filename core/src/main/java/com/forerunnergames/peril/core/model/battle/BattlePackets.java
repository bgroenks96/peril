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

package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.battle.FinalBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultBattleResultPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultFinalBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultPendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.annotations.AllowNegative;

public final class BattlePackets
{
  public static PendingBattleActorPacket from (final PendingBattleActor actor,
                                               final PlayerModel playerModel,
                                               final CountryGraphModel graphModel)
  {
    Arguments.checkIsNotNull (actor, "actor");
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (graphModel, "graphModel");

    final PlayerPacket player = playerModel.playerPacketWith (actor.getPlayerId ());
    final CountryPacket country = graphModel.countryPacketWith (actor.getCountryId ());

    return new DefaultPendingBattleActorPacket (player, country, actor.getDieRange ());
  }

  public static FinalBattleActorPacket from (final FinalBattleActor actor,
                                             final PlayerModel playerModel,
                                             final CountryGraphModel graphModel)
  {
    Arguments.checkIsNotNull (actor, "actor");
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (graphModel, "graphModel");

    final PlayerPacket player = playerModel.playerPacketWith (actor.getPlayerId ());
    final CountryPacket country = graphModel.countryPacketWith (actor.getCountryId ());

    return new DefaultFinalBattleActorPacket (player, country, actor.getDieRange (), actor.getDieCount ());
  }

  public static BattleResultPacket from (final BattleResult result,
                                         final PlayerModel playerModel,
                                         final CountryGraphModel countryGraphModel,
                                         @AllowNegative final int attackingCountryArmyDelta,
                                         @AllowNegative final int defendingCountryArmyDelta)
  {
    Arguments.checkIsNotNull (result, "result");
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (countryGraphModel, "countryGraphModel");

    final FinalBattleActorPacket attacker = from (result.getAttacker (), playerModel, countryGraphModel);
    final FinalBattleActorPacket defender = from (result.getDefender (), playerModel, countryGraphModel);

    return new DefaultBattleResultPacket (result.getOutcome (), attacker, defender,
            playerModel.playerPacketWith (result.getDefendingCountryOwner ()), result.getAttackerRolls (),
            result.getDefenderRolls (), attackingCountryArmyDelta, defendingCountryArmyDelta);
  }

  private BattlePackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
