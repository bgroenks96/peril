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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.countrycounter;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.CountryNamesDataLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.AbsolutePlayMapResourcesPathParser;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CountryCounterFactory
{
  public static CountryCounter create (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return new DefaultCountryCounter (new CountryNamesDataLoader (new ExternalStreamParserFactory ()),
            new AbsolutePlayMapResourcesPathParser (gameMode));
  }

  private CountryCounterFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
