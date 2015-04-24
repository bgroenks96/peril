package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.DefaultScreenMusic;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PerilPlayScreenFactory
{
  public static Screen create (final ScreenController screenController,
                               final ScreenSize screenSize,
                               final MouseInput mouseInput,
                               final Skin skin,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new PerilPlayScreen (screenController, new TankActor2 (), screenSize, mouseInput, new DefaultScreenMusic (
            Assets.playScreenMusic, new MusicSettings ()), skin, eventBus);
  }

  private PerilPlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
