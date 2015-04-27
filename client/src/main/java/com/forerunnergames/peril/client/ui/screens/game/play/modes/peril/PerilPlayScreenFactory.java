package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PerilPlayScreenFactory
{
  public static Screen create (final ScreenChanger screenChanger,
                               final ScreenSize screenSize,
                               final MouseInput mouseInput,
                               final Skin skin,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new PerilPlayScreen (screenChanger, new TankActor2 (), screenSize, mouseInput, skin, eventBus);
  }

  private PerilPlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
