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

package com.forerunnergames.peril.common.net.events.client.request.inform;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerFortifyCountryEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerFortifyCountryRequestEvent implements PlayerInformRequestEvent <PlayerFortifyCountryEvent>
{
  private final int deltaArmyCount;

  public PlayerFortifyCountryRequestEvent (final int deltaArmyCount)
  {
    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.deltaArmyCount = deltaArmyCount;
  }

  @Override
  public Class <PlayerFortifyCountryEvent> getQuestionType ()
  {
    return PlayerFortifyCountryEvent.class;
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: DeltaArmyCount: {}", getClass ().getSimpleName (), deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryRequestEvent ()
  {
    deltaArmyCount = 0;
  }
}
