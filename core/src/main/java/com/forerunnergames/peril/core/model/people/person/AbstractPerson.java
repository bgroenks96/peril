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

package com.forerunnergames.peril.core.model.people.person;

import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPerson extends AbstractAsset implements Person
{
  private PersonIdentity identity = PersonIdentity.UNKNOWN;

  protected AbstractPerson (final String name, final Id id, final PersonIdentity identity)
  {
    super (name, id);

    Arguments.checkIsNotNull (identity, "identity");

    this.identity = identity;
  }

  @RequiredForNetworkSerialization
  protected AbstractPerson ()
  {
  }

  @Override
  public PersonIdentity getIdentity ()
  {
    return identity;
  }

  @Override
  public void setIdentity (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    this.identity = identity;
  }

  @Override
  public boolean has (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    return this.identity == identity;
  }
}
