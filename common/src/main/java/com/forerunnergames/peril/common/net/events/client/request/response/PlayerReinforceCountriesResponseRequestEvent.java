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

import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.collect.ImmutableMap;

public final class PlayerReinforceCountriesResponseRequestEvent implements ResponseRequestEvent
{
  private final ImmutableMap <String, Integer> countryNamesToReinforcements;

  public PlayerReinforceCountriesResponseRequestEvent (final ImmutableMap <String, Integer> countryNamesToReinforcements)
  {
    Arguments.checkIsNotNull (countryNamesToReinforcements, "countryNamesToReinforcements");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToReinforcements, "countryNamesToReinforcements");

    this.countryNamesToReinforcements = countryNamesToReinforcements;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerReinforceCountriesRequestEvent.class;
  }

  public ImmutableMap <String, Integer> getReinforcedCountries ()
  {
    return countryNamesToReinforcements;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Reinforcements: [{}]", getClass ().getSimpleName (), countryNamesToReinforcements);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesResponseRequestEvent ()
  {
    countryNamesToReinforcements = null;
  }
}
