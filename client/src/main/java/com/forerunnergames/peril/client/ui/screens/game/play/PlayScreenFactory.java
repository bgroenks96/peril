package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreen;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.PerilModePlayScreenFactory;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PlayScreenFactory
{
  public static Screen create (final GameMode gameMode,
                               final ScreenChanger screenChanger,
                               final ScreenSize screenSize,
                               final MouseInput mouseInput,
                               final Batch batch,
                               final AssetManager assetManager,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    switch (gameMode)
    {
      case CLASSIC:
      {
        return new ClassicModePlayScreen (screenChanger, screenSize, mouseInput, batch, assetManager, eventBus);
      }
      case PERIL:
      {
        return PerilModePlayScreenFactory.create (screenChanger, screenSize, mouseInput, batch, assetManager, eventBus);
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
