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
