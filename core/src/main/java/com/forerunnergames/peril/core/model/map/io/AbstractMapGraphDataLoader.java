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
import com.forerunnergames.peril.core.model.map.TerritoryGraphModel;
import com.forerunnergames.peril.core.model.map.territory.Territory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMapGraphDataLoader <T extends Territory, U extends TerritoryGraphModel <T>> extends
        AbstractDataLoader <U>
{
  private static final Logger log = LoggerFactory.getLogger (AbstractMapGraphDataLoader.class);
  private final DefaultGraphModel.Builder <T> adjListBuilder = DefaultGraphModel.builder ();
  private final StreamParserFactory streamParserFactory;
  private final ImmutableMap <String, T> namesToData;
  private StreamParser streamParser;
  private List <String> lineData;

  public AbstractMapGraphDataLoader (final StreamParserFactory streamParserFactory, final ImmutableSet <T> data)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");
    Arguments.checkIsNotNull (data, "data");
    Arguments.checkHasNoNullElements (data, "data");

    final ImmutableMap.Builder <String, T> mapBuilder = ImmutableMap.builder ();
    for (final T next : data)
    {
      mapBuilder.put (next.getName (), next);
    }
    namesToData = mapBuilder.build ();

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected U finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return createGraphModel (adjListBuilder.build ());
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    streamParser = streamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    lineData = new ArrayList <> (streamParser.getNextRemainingQuotedStringsOnLine ());
    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    assert lineData != null;

    if (lineData.size () <= 0) return;

    final String firstName = lineData.remove (0);
    if (!namesToData.containsKey (firstName))
    {
      log.warn ("Skipping list data for unrecognized node [{}].", firstName);
      return;
    }

    final T first = namesToData.get (firstName);
    for (final String nodeName : lineData)
    {
      if (!namesToData.containsKey (nodeName))
      {
        log.warn ("Ignoring unrecognized node [{}] in adjacency list for [{}].", nodeName, firstName);
        continue;
      }
      adjListBuilder.setAdjacent (first, namesToData.get (nodeName));
    }

    log.debug ("Successfully loaded graph data for {}: {}", firstName, lineData);
  }

  protected abstract U createGraphModel (final GraphModel <T> internalGraphModel);
}
