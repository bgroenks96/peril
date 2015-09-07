package com.forerunnergames.peril.core.model.card.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.core.model.map.io.CoreMapDataPathParser;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CardModelDataFactoryCreator
{
  public static CardModelDataFactory create (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return new DefaultCardModelDataFactory (new CoreMapDataPathParser (gameMode));
  }

  private CardModelDataFactoryCreator ()
  {
    Classes.instantiationNotAllowed ();
  }
}
