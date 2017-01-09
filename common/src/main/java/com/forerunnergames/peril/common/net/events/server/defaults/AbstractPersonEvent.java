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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.PersonEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPersonEvent <T extends PersonPacket> implements PersonEvent <T>
{
  private final T person;

  protected AbstractPersonEvent (final T person)
  {
    Arguments.checkIsNotNull (person, "person");

    this.person = person;
  }

  @RequiredForNetworkSerialization
  protected AbstractPersonEvent ()
  {
    person = null;
  }

  @Override
  public final T getPerson ()
  {
    return person;
  }

  @Override
  public final String getPersonName ()
  {
    return person.getName ();
  }

  @Override
  public final PersonSentience getPersonSentience ()
  {
    return person.getSentience ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Person: [{}]", getClass ().getSimpleName (), person);
  }
}
