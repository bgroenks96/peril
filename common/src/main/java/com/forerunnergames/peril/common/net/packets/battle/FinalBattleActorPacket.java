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

package com.forerunnergames.peril.common.net.packets.battle;

/**
 * A battle actor (player + country) that has a final die count. The final die count is guaranteed to be within the
 * range of {@link #getDieRange()}, which represents the valid die range BEFORE the attack / die-roll occurred.
 *
 * {@see com.forerunnergames.peril.core.model.battle.FinalBattleActor}
 * {@see com.forerunnergames.peril.core.model.battle.PendingBattleActor}
 * {@see com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket}
 */
public interface FinalBattleActorPacket extends PendingBattleActorPacket
{
  int getDieCount ();
}
