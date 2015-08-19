package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.PerilModePlayScreenWidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PerilModePlayScreenFactory
{
  public static Screen create (final ScreenChanger screenChanger,
                               final ScreenSize screenSize,
                               final MouseInput mouseInput,
                               final Batch batch,
                               final AssetManager assetManager,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new PerilModePlayScreen (new PerilModePlayScreenWidgetFactory (assetManager), screenChanger, screenSize,
            mouseInput, batch, eventBus);
  }

  private PerilModePlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
