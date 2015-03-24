package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicPlayScreenFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.PerilPlayScreenFactory;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PlayScreenFactory
{
  public static Screen create (final ScreenController screenController,
                               final MouseInput mouseInput,
                               final GameMode gameMode,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    switch (gameMode)
    {
    case CLASSIC:
    {
      return ClassicPlayScreenFactory.create (screenController, mouseInput, eventBus);
    }
    case PERIL:
    {
      return PerilPlayScreenFactory.create (screenController, mouseInput, eventBus);
    }
    default:
    {
      throw new UnsupportedOperationException ("Unknown game mode [" + gameMode + "].");
    }
    }
  }

  private PlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
