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
  private int typeValue;

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
    typeValue = parser.getNextInteger ();

    log.trace ("Parsed data: [name={}] [type={}].", name, typeValue);

    return !parser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    final Card card = CardFactory.create (name, CardType.fromValue (typeValue));

    log.debug ("Successfully loaded data [{}] from file [{}].", card, fileName);

    cardsBuilder.put (card.getId (), card);
  }
}
