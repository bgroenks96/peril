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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.common.io.AbstractBiMapDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public abstract class AbstractTerritoryColorToNameLoader <T extends TerritoryColor <?>>
        extends AbstractBiMapDataLoader <T, String> implements TerritoryColorToNameLoader <T>
{
  private final ImmutableBiMap.Builder <T, String> territoryColorsToNames = new ImmutableBiMap.Builder <> ();
  private final StreamParserFactory streamParserFactory;
  private StreamParser streamParser;
  private int territoryColorComponentValue;
  private String territoryName;

  protected AbstractTerritoryColorToNameLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected final ImmutableBiMap <T, String> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return territoryColorsToNames.build ();
  }

  @Override
  protected final void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    streamParser = streamParserFactory.create (fileName);
  }

  @Override
  protected final boolean readData ()
  {
    territoryName = streamParser.getNextQuotedString ();
    territoryColorComponentValue = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected final void saveData ()
  {
    territoryColorsToNames.put (createTerritoryColor (territoryColorComponentValue), territoryName);
  }

  protected abstract T createTerritoryColor (final int colorComponentValue);
}
