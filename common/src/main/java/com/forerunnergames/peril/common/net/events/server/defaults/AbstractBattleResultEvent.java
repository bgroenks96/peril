/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleResultEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.battle.FinalBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractBattleResultEvent implements BattleResultEvent
{
  final BattleResultPacket battleResult;

  public AbstractBattleResultEvent (final BattleResultPacket battleResult)
  {
    Arguments.checkIsNotNull (battleResult, "battleResult");

    this.battleResult = battleResult;
  }

  @RequiredForNetworkSerialization
  protected AbstractBattleResultEvent ()
  {
    battleResult = null;
  }

  @Override
  public BattleResultPacket getBattleResult ()
  {
    return battleResult;
  }

  @Override
  public BattleOutcome getBattleOutcome ()
  {
    return battleResult.getOutcome ();
  }

  @Override
  public boolean battleOutcomeIs (final BattleOutcome outcome)
  {
    Arguments.checkIsNotNull (outcome, "outcome");

    return battleResult.outcomeIs (outcome);
  }

  @Override
  public FinalBattleActorPacket getAttacker ()
  {
    return battleResult.getAttacker ();
  }

  @Override
  public FinalBattleActorPacket getDefender ()
  {
    return battleResult.getDefender ();
  }

  @Override
  public int getAttackerDieCount ()
  {
    return battleResult.getAttackerDieCount ();
  }

  @Override
  public int getDefenderDieCount ()
  {
    return battleResult.getDefenderDieCount ();
  }

  @Override
  public PlayerPacket getAttackingPlayer ()
  {
    return battleResult.getAttackingPlayer ();
  }

  @Override
  public PlayerPacket getDefendingPlayer ()
  {
    return battleResult.getDefendingPlayer ();
  }

  @Override
  public String getAttackingPlayerName ()
  {
    return battleResult.getAttackingPlayerName ();
  }

  @Override
  public String getDefendingPlayerName ()
  {
    return battleResult.getDefendingPlayerName ();
  }

  @Override
  public PlayerColor getAttackingPlayerColor ()
  {
    return battleResult.getAttackingPlayerColor ();
  }

  @Override
  public PlayerColor getDefendingPlayerColor ()
  {
    return battleResult.getDefendingPlayerColor ();
  }

  @Override
  public int getAttackingPlayerCardsInHand ()
  {
    return battleResult.getAttackingPlayerCardsInHand ();
  }

  @Override
  public int getDefendingPlayerCardsInHand ()
  {
    return battleResult.getDefendingPlayerCardsInHand ();
  }

  @Override
  public CountryPacket getAttackingCountry ()
  {
    return battleResult.getAttackingCountry ();
  }

  @Override
  public CountryPacket getDefendingCountry ()
  {
    return battleResult.getDefendingCountry ();
  }

  @Override
  public String getAttackingCountryName ()
  {
    return battleResult.getAttackingCountryName ();
  }

  @Override
  public String getDefendingCountryName ()
  {
    return battleResult.getDefendingCountryName ();
  }

  @Override
  public int getAttackingCountryArmyCount ()
  {
    return battleResult.getAttackingCountryArmyCount ();
  }

  @Override
  public int getDefendingCountryArmyCount ()
  {
    return battleResult.getDefendingCountryArmyCount ();
  }

  @Override
  public int getAttackingCountryArmyDelta ()
  {
    return battleResult.getAttackingCountryArmyDelta ();
  }

  @Override
  public int getDefendingCountryArmyDelta ()
  {
    return battleResult.getDefendingCountryArmyDelta ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | BattleResult: [{}]", getClass ().getSimpleName (), battleResult);
  }
}
