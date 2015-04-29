package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.PerilModePlayScreenFactory;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PlayScreenFactory
{
  public static Screen create (final GameMode gameMode,
                               final Skin skin,
                               final ScreenChanger screenChanger,
                               final ScreenSize screenSize,
                               final MouseInput mouseInput,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    switch (gameMode)
    {
      case CLASSIC:
      {
        return ClassicModePlayScreenFactory.create (skin, screenChanger, screenSize, mouseInput, eventBus);
      }
      case PERIL:
      {
        return PerilModePlayScreenFactory.create (skin, screenChanger, screenSize, mouseInput, eventBus);
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
