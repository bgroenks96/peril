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

package com.forerunnergames.peril.common.net.events.defaults;

import com.forerunnergames.peril.common.net.events.client.interfaces.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractJoinGameServerRequestEvent implements JoinGameServerRequestEvent
{
  private final PersonSentience personSentience;

  protected AbstractJoinGameServerRequestEvent (final PersonSentience personSentience)
  {
    Arguments.checkIsNotNull (personSentience, "personSentience");

    this.personSentience = personSentience;
  }

  @RequiredForNetworkSerialization
  protected AbstractJoinGameServerRequestEvent ()
  {
    personSentience = null;
  }

  @Override
  public PersonSentience getPersonSentience ()
  {
    return personSentience;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: PersonSentience: [{}]", getClass ().getSimpleName (), personSentience);
  }
}
