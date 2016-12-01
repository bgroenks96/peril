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

package com.forerunnergames.peril.core.model.card.io;

import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.core.model.io.InternalStreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

public final class CardModelDataLoaderFactory
{
  public static CardModelDataLoader create (final PlayMapType playMapType)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");

    final StreamParserFactory streamParserFactory;

    switch (playMapType)
    {
      case STOCK:
      {
        streamParserFactory = new InternalStreamParserFactory ();
        break;
      }
      case CUSTOM:
      {
        streamParserFactory = new ExternalStreamParserFactory ();
        break;
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Cannot create {} for {}: [{}].", CardModelDataLoader.class.getSimpleName (),
                                PlayMapType.class.getSimpleName (), playMapType));
      }
    }

    return new CardModelDataLoader (streamParserFactory);
  }

  private CardModelDataLoaderFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
