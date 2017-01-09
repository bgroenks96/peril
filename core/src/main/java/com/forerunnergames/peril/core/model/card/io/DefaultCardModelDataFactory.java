/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.io.PlayMapDataPathParser;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class DefaultCardModelDataFactory implements CardModelDataFactory
{
  private final PlayMapDataPathParser playMapDataPathParser;

  DefaultCardModelDataFactory (final PlayMapDataPathParser playMapDataPathParser)
  {
    Arguments.checkIsNotNull (playMapDataPathParser, "playMapDataPathParser");

    this.playMapDataPathParser = playMapDataPathParser;
  }

  @Override
  public ImmutableSet <Card> createCards (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    final CardModelDataLoader loader = CardModelDataLoaderFactory.create (playMapMetadata.getType ());

    return ImmutableSet.copyOf (loader.load (playMapDataPathParser.parseCardsFileNamePath (playMapMetadata)).values ());
  }
}
