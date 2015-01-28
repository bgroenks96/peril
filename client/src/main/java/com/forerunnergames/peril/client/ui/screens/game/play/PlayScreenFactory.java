package com.forerunnergames.peril.client.ui.screens.game.play;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActorFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetectionFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class PlayScreenFactory
{
  public static PlayScreen create (final ScreenController screenController, final MouseInput mouseInput)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");

    final PlayMapInputDetection playMapInputDetection = PlayMapInputDetectionFactory.create ();

    return new PlayScreen (screenController, PlayMapActorFactory.create (playMapInputDetection),
                    new TerritoryTextActor (playMapInputDetection, mouseInput), new PlayScreenMusic ());
  }

  private PlayScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
