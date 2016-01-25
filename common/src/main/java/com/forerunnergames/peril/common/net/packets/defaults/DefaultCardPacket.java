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

package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.AbstractAssetPacket;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import java.util.UUID;

public class DefaultCardPacket extends AbstractAssetPacket implements CardPacket
{
  private final int type;

  public DefaultCardPacket (final String name, final int type, final UUID id)
  {
    super (name, id);

    Arguments.checkIsNotNegative (type, "type");

    this.type = type;
  }

  @Override
  public int getType ()
  {
    return type;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Type: {}", super.toString (), type);
  }

  @Override
  public boolean typeIs (final int type)
  {
    return this.type == type;
  }
}
