package com.forerunnergames.peril.client.ui.screens.game.play;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActorFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetectionFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PlayScreenFactory
{
  public static PlayScreen create (final ScreenController screenController,
                                   final MouseInput mouseInput,
                                   final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    final PlayMapInputDetection playMapInputDetection = PlayMapInputDetectionFactory.create ();

    return new PlayScreen (screenController, new PlayScreenWidgetFactory (Assets.skin, eventBus),
        PlayMapActorFactory.create (playMapInputDetection), new TerritoryTextActor (playMapInputDetection, mouseInput),
        new PlayScreenMusic (), eventBus);
  }

  private PlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
