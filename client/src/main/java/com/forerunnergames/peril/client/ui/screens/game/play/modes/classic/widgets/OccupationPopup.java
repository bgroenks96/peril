package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public class OccupationPopup extends AbstractArmyMovementPopup
{
  private static final String TITLE = "Occupation";

  public OccupationPopup (final Skin skin,
                          final ClassicModePlayScreenWidgetFactory widgetFactory,
                          final Stage stage,
                          final PopupListener listener,
                          final MBassador <Event> eventBus)
  {
    super (skin, TITLE, widgetFactory, stage, listener, eventBus);
  }
}
