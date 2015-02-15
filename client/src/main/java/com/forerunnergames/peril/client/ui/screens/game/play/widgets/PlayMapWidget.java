package com.forerunnergames.peril.client.ui.screens.game.play.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.tools.common.Arguments;

public final class PlayMapWidget extends Stack
{
  public PlayMapWidget (final Image backgroundImage,
                        final PlayMapActor playMapActor,
                        final TerritoryTextActor territoryTextActor)
  {
    Arguments.checkIsNotNull (backgroundImage, "backgroundImage");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");

    add (backgroundImage);
    add (playMapActor);
    add (territoryTextActor);
  }
}
