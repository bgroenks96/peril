package com.forerunnergames.peril.core.model.card.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class DefaultCardModelDataFactory implements CardModelDataFactory
{
  private final MapDataPathParser mapDataPathParser;

  DefaultCardModelDataFactory (final MapDataPathParser mapDataPathParser)
  {
    Arguments.checkIsNotNull (mapDataPathParser, "mapDataPathParser");

    this.mapDataPathParser = mapDataPathParser;
  }

  @Override
  public ImmutableSet <Card> createCards (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    final CardModelDataLoader loader = CardModelDataLoaderFactory.create (mapMetadata.getType ());

    return ImmutableSet.copyOf (loader.load (mapDataPathParser.parseCardsFileNamePath (mapMetadata)).values ());
  }
}
