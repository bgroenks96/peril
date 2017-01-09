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

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.tools.common.id.Id;

/**
 * A battle actor (player + country) that does not yet have a final die count. The final die count is guaranteed to be
 * within the range of {@link #getDieRange()}.
 *
 * {@see com.forerunnergames.peril.core.model.battle.FinalBattleActor}
 * {@see com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket}
 * {@see com.forerunnergames.peril.common.net.packets.battle.FinalBattleActorPacket}
 */
public interface PendingBattleActor
{
  Id getPlayerId ();

  Id getCountryId ();

  DieRange getDieRange ();
}
