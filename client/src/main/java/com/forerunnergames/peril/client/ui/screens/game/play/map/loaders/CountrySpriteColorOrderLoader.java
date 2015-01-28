package com.forerunnergames.peril.client.ui.screens.game.play.map.loaders;

import com.forerunnergames.peril.client.io.AbstractDataLoader;
import com.forerunnergames.peril.client.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public final class CountrySpriteColorOrderLoader extends AbstractDataLoader <PlayerColor, Integer>
{
  private final ImmutableBiMap.Builder <PlayerColor, Integer> countrySpriteOrderBuilder = new ImmutableBiMap.Builder <> ();
  private StreamParser streamParser;
  private String spriteColorValue;
  private int spriteIndex;

  @Override
  protected ImmutableBiMap <PlayerColor, Integer> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return countrySpriteOrderBuilder.build ();
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    streamParser = StreamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    spriteColorValue = streamParser.getNextUnquotedString ();
    spriteIndex = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    final PlayerColor spriteColor = PlayerColor.valueOf (spriteColorValue.toUpperCase ());

    countrySpriteOrderBuilder.put (spriteColor, spriteIndex);
  }
}
