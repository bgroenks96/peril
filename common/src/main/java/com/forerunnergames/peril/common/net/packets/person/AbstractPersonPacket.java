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

package com.forerunnergames.peril.common.net.packets.person;

import com.forerunnergames.peril.common.net.packets.AbstractAssetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public abstract class AbstractPersonPacket extends AbstractAssetPacket implements PersonPacket
{
  private PersonIdentity identity = PersonIdentity.UNKNOWN;

  protected AbstractPersonPacket (final String name, final UUID id)
  {
    super (name, id);
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

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Person Identity: {}", super.toString (), identity);
  }

  @RequiredForNetworkSerialization
  protected AbstractPersonPacket ()
  {
    identity = null;
  }
}
