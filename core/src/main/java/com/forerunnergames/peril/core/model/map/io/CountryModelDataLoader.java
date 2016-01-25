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

package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.io.AbstractDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CountryModelDataLoader extends AbstractDataLoader <CountryFactory>
{
  private static final Logger log = LoggerFactory.getLogger (CountryModelDataLoader.class);
  private final CountryFactory factory = new CountryFactory ();
  private final StreamParserFactory streamParserFactory;
  private StreamParser streamParser;
  private String fileName;
  private String name;

  public CountryModelDataLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected CountryFactory finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return factory;
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    this.fileName = fileName;
    streamParser = streamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    name = streamParser.getNextQuotedString ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    factory.newCountryWith (name);

    log.debug ("Successfully loaded country [{}] from file [{}].", name, fileName);
  }
}
