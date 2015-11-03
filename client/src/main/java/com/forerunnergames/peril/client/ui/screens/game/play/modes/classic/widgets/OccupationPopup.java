package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public class OccupationPopup extends AbstractArmyMovementPopup
{
  private static final String TITLE = "Occupation";

  public OccupationPopup (final ClassicModePlayScreenWidgetFactory widgetFactory,
                          final Stage stage,
                          final PopupListener listener,
                          final MBassador <Event> eventBus)
  {
    super (widgetFactory, TITLE, stage, listener, eventBus);
  }
}
