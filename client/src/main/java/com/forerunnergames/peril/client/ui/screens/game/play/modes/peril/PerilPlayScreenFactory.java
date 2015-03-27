package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.PlayScreenMusic;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PerilPlayScreenFactory
{
  public static Screen create (final ScreenController screenController,
                               final MouseInput mouseInput,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new PerilPlayScreen (screenController, new BattleGrid (new TankActor ()), new PlayScreenMusic (), eventBus);
  }

  private PerilPlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
