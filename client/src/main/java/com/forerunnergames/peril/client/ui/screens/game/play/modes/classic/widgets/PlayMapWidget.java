package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.ArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.TerritoryTextActor;
import com.forerunnergames.tools.common.Arguments;

public final class PlayMapWidget extends Stack
{
  public PlayMapWidget (final Image backgroundImage,
                        final PlayMapActor playMapActor,
                        final ArmyTextActor armyTextActor,
                        final TerritoryTextActor territoryTextActor)
  {
    Arguments.checkIsNotNull (backgroundImage, "backgroundImage");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (armyTextActor, "armyTextActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");

    add (backgroundImage);
    add (playMapActor);
    add (armyTextActor);
    add (territoryTextActor);
  }
}
