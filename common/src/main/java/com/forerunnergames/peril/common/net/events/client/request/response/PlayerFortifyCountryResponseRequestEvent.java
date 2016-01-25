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

package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerFortifyCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.base.Optional;

public final class PlayerFortifyCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final Optional <String> sourceCountry;
  private final Optional <String> targetCountry;
  private final int fortifyArmyCount;

  public PlayerFortifyCountryResponseRequestEvent (final String sourceCountry,
                                                   final String targetCountry,
                                                   final int fortifyArmyCount)
  {
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkLowerExclusiveBound (fortifyArmyCount, 0, "fortifyArmyCount");

    this.sourceCountry = Optional.of (sourceCountry);
    this.targetCountry = Optional.of (targetCountry);
    this.fortifyArmyCount = fortifyArmyCount;
  }

  /**
   * Empty response constructor signifies that no fortification move was made.
   */
  public PlayerFortifyCountryResponseRequestEvent ()
  {
    sourceCountry = Optional.absent ();
    targetCountry = Optional.absent ();
    fortifyArmyCount = 0;
  }

  public boolean isCountryDataPresent ()
  {
    return sourceCountry.isPresent () && targetCountry.isPresent ();
  }

  public Optional <String> getSourceCountry ()
  {
    return sourceCountry;
  }

  public Optional <String> getTargetCountry ()
  {
    return targetCountry;
  }

  public int getFortifyArmyCount ()
  {
    return fortifyArmyCount;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerFortifyCountryRequestEvent.class;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: SourceCountry: {} | TargetCountry: {} | FortifyArmyCount: {}",
                           getClass ().getSimpleName (), sourceCountry, targetCountry, fortifyArmyCount);
  }
}
