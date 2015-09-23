package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public class ReinforcementPopup extends AbstractArmyMovementPopup
{
  private static final String TITLE = "Reinforcement";

  public ReinforcementPopup (final Skin skin,
                             final ClassicModePlayScreenWidgetFactory widgetFactory,
                             final Stage stage,
                             final PopupListener listener,
                             final MBassador <Event> eventBus)

  {
    super (skin, TITLE, widgetFactory, stage, listener, eventBus);
  }

  @Override
  protected void addButtons ()
  {
    addButton ("CANCEL", PopupAction.HIDE);

    super.addButtons ();
  }

  @Override
  protected void addKeys ()
  {
    super.addKeys ();

    addKey (Input.Keys.ESCAPE, PopupAction.HIDE);
  }
}
