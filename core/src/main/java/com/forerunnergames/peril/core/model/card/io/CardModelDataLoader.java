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

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.io.AbstractBiMapDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CardModelDataLoader extends AbstractBiMapDataLoader <Id, Card>
{
  private static final Logger log = LoggerFactory.getLogger (CardModelDataLoader.class);
  private final ImmutableBiMap.Builder <Id, Card> cardsBuilder = ImmutableBiMap.builder ();
  private final StreamParserFactory streamParserFactory;
  private StreamParser parser;
  private String fileName;
  private String name;
  private int idValue;

  public CardModelDataLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected ImmutableBiMap <Id, Card> finalizeData ()
  {
    parser.verifyEndOfFile ();
    parser.close ();

    return cardsBuilder.build ();
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    log.trace ("Initializing [{}] with file [{}].", getClass ().getSimpleName (), fileName);

    this.fileName = fileName;
    parser = streamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    name = parser.getNextQuotedString ();
    idValue = parser.getNextInteger ();

    log.trace ("Parsed data: [name={}] [type={}].", name, idValue);

    return !parser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    final Card card = CardFactory.create (name, CardType.fromId (idValue));

    log.debug ("Successfully loaded data [{}] from file [{}].", card, fileName);

    cardsBuilder.put (card.getId (), card);
  }
}
