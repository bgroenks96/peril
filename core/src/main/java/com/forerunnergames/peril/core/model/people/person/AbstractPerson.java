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

package com.forerunnergames.peril.core.model.people.person;

import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPerson extends AbstractAsset implements Person
{
  private final PersonSentience sentience;

  protected AbstractPerson (final String name, final Id id, final PersonSentience sentience)
  {
    super (name, id);

    Arguments.checkIsNotNull (sentience, "sentience");

    this.sentience = sentience;
  }

  @RequiredForNetworkSerialization
  protected AbstractPerson ()
  {
    sentience = null;
  }

  @Override
  public final PersonSentience getSentience ()
  {
    return sentience;
  }

  @Override
  public final boolean has (final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (sentience, "sentience");

    return this.sentience == sentience;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: PersonSentience: [{}]", super.toString ());
  }
}
