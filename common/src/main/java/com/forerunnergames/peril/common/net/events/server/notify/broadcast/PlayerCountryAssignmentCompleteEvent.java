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

package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class PlayerCountryAssignmentCompleteEvent implements BroadcastNotificationEvent
{
  private final InitialCountryAssignment assignmentMode;
  private final ImmutableMap <CountryPacket, PlayerPacket> countryToPlayerPackets;

  public PlayerCountryAssignmentCompleteEvent (final InitialCountryAssignment assignmentMode,
                                               final ImmutableMap <CountryPacket, PlayerPacket> countryToPlayerPackets)
  {
    Arguments.checkIsNotNull (assignmentMode, "assignmentMode");
    Arguments.checkIsNotNull (countryToPlayerPackets, "countryToPlayerPackets");
    Arguments.checkHasNoNullKeysOrValues (countryToPlayerPackets, "countryToPlayerPackets");

    this.assignmentMode = assignmentMode;
    this.countryToPlayerPackets = countryToPlayerPackets;
  }

  public ImmutableSet <CountryPacket> getCountries ()
  {
    return countryToPlayerPackets.keySet ();
  }

  public ImmutableSet <PlayerPacket> getPlayers ()
  {
    return ImmutableSet.copyOf (countryToPlayerPackets.values ());
  }

  public PlayerPacket getOwner (final CountryPacket country)
  {
    Arguments.checkIsNotNull (country, "country");

    return countryToPlayerPackets.get (country);
  }

  public InitialCountryAssignment getAssignmentMode ()
  {
    return assignmentMode;
  }

  public boolean assignmentModeIs (final InitialCountryAssignment assignmentMode)
  {
    Arguments.checkIsNotNull (assignmentMode, "assignmentMode");

    return this.assignmentMode == assignmentMode;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: AssignmentMode: {} | CountryToPlayerPackets: [{}]", getClass ().getSimpleName (),
                           assignmentMode, Strings.toString (countryToPlayerPackets));
  }

  @RequiredForNetworkSerialization
  private PlayerCountryAssignmentCompleteEvent ()
  {
    assignmentMode = null;
    countryToPlayerPackets = null;
  }
}
