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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageData;
import com.forerunnergames.peril.common.io.AbstractBiMapDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public final class CountryImageDataLoader extends AbstractBiMapDataLoader <String, CountryImageData>
{
  private final StreamParserFactory streamParserFactory;
  private ImmutableBiMap.Builder <String, CountryImageData> countryImageDataBuilder;
  private StreamParser streamParser;
  private String nameValue;
  private int referenceWidth;
  private int referenceHeight;
  private int referenceDestinationX;
  private int referenceDestinationY;
  private int referenceTextUpperLeftX;
  private int referenceTextUpperLeftY;

  public CountryImageDataLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected ImmutableBiMap <String, CountryImageData> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return countryImageDataBuilder.build ();
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    countryImageDataBuilder = new ImmutableBiMap.Builder <> ();
    streamParser = streamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    nameValue = streamParser.getNextQuotedString ();
    referenceWidth = streamParser.getNextInteger ();
    referenceHeight = streamParser.getNextInteger ();
    referenceDestinationX = streamParser.getNextInteger ();
    referenceDestinationY = streamParser.getNextInteger ();
    referenceTextUpperLeftX = streamParser.getNextInteger ();
    referenceTextUpperLeftY = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    final Vector2 referenceSize = new Vector2 (referenceWidth, referenceHeight);
    final Vector2 referenceDestination = new Vector2 (referenceDestinationX, referenceDestinationY);
    final Vector2 referenceTextUpperLeft = new Vector2 (referenceTextUpperLeftX, referenceTextUpperLeftY);

    countryImageDataBuilder
            .put (nameValue,
                  new CountryImageData (nameValue, referenceDestination, referenceTextUpperLeft, referenceSize));
  }
}
