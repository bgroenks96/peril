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

package com.forerunnergames.peril.common.game;

/**
 * A battle outcome occurs after every attack (die roll) on a specific vector (the immutable set of attacking player,
 * defending player, attacking country, & defending country).
 *
 * There are 3 possible outcomes:
 *
 * <pre>
 * 1. The attacker is victorious, has conquered the defending country, & can no longer attack the vector.
 * 2. The attacker is defeated, has not conquered the defending country, & can no longer attack the vector.
 * 3. The attacker is neither victorious nor defeated, has not conquered the defending country, and may continue attacking the vector.
 * </pre>
 */
public enum BattleOutcome
{
  ATTACKER_VICTORIOUS,
  ATTACKER_DEFEATED,
  CONTINUE
}
