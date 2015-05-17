package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ClassicModePlayScreenFactory
{
  public static Screen create (final Skin skin,
                               final ScreenChanger screenChanger,
                               final ScreenSize screenSize,
                               final MouseInput mouseInput,
                               final Batch batch,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new ClassicModePlayScreen (new ClassicModePlayScreenWidgetFactory (skin), screenChanger, screenSize,
            mouseInput, batch, eventBus);
  }

  private ClassicModePlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
