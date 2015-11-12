package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.io.AbstractDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.territory.Territory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractMapGraphDataLoader <T extends Territory> extends AbstractDataLoader <T, Iterable <T>>
{
  private final ImmutableBiMap.Builder <T, Iterable <T>> adjListBuilder = new ImmutableBiMap.Builder <> ();
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

  @Override
  protected ImmutableBiMap <T, Iterable <T>> finalizeData ()
  {
    return adjListBuilder.build ();
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
    final ImmutableList.Builder <T> listBuilder = ImmutableList.builder ();
    while (lineDataItr.hasNext ())
    {
      listBuilder.add (namesToData.get (lineDataItr.next ()));
    }
    lineData = listBuilder.build ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    assert lineData != null;

    final T first = lineData.remove (0);
    adjListBuilder.put (first, lineData);
  }
}
