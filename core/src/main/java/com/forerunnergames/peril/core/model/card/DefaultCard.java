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

package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultCard extends AbstractAsset implements Card
{
  private final CardType type;

  public DefaultCard (final String name, final Id id, final CardType type)
  {
    super (name, id);

    Arguments.checkIsNotNull (type, "type");

    this.type = type;
  }

  @Override
  public CardType getType ()
  {
    return type;
  }

  @Override
  public boolean typeIs (final CardType level)
  {
    Arguments.checkIsNotNull (level, "level");

    return type == level;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Type: {}", super.toString (), type.toString ());
  }
}
