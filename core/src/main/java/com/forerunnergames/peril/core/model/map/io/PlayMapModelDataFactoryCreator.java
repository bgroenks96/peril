package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class PlayMapModelDataFactoryCreator
{
  public static PlayMapModelDataFactory create (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return new DefaultPlayMapModelDataFactory (new CoreMapDataPathParser (gameMode));
  }

  private PlayMapModelDataFactoryCreator ()
  {
    Classes.instantiationNotAllowed ();
  }
}
