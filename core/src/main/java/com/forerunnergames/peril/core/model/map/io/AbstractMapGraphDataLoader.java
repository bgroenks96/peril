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
import java.util.Iterator;
import java.util.List;

public abstract class AbstractMapGraphDataLoader <T extends Territory, U extends TerritoryGraphModel <T>>
        extends AbstractDataLoader <U>
{
  private final DefaultGraphModel.Builder <T> adjListBuilder = DefaultGraphModel.builder ();
  private final StreamParserFactory streamParserFactory;
  private final ImmutableMap <String, T> namesToData;
  private StreamParser streamParser;
  private List <T> lineData;

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

  protected abstract U finalizeData (final GraphModel <T> internalGraphModel);

  @Override
  protected U finalizeData ()
  {
    return finalizeData (adjListBuilder.build ());
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
    final Iterator <String> lineDataItr = streamParser.getNextRemainingQuotedStringsOnLine ().iterator ();
    lineData = new ArrayList <> ();
    while (lineDataItr.hasNext ())
    {
      lineData.add (namesToData.get (lineDataItr.next ()));
    }

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    assert lineData != null;

    final T first = lineData.remove (0);
    for (final T node : lineData)
    {
      adjListBuilder.setAdjacent (first, node);
    }
  }
}
