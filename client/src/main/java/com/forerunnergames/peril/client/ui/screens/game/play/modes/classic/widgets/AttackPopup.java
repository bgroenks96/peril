package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.forerunnergames.peril.client.ui.widgets.popup.KeyListener;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class AttackPopup extends AbstractBattlePopup
{
  private final AttackPopupListener listener;
  private TextButton quitButton;

  public AttackPopup (final BattlePopupWidgetFactory widgetFactory,
                      final Stage stage,
                      final AttackPopupListener listener,
                      final MBassador <Event> eventBus)
  {
    super (widgetFactory, new AttackPopupDiceFactory (widgetFactory), "Attack", stage, listener, eventBus);

    Arguments.checkIsNotNull (listener, "listener");

    this.listener = listener;
  }

  @Override
  protected void addButtons ()
  {
    quitButton = addTextButton ("Retreat", PopupAction.HIDE, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        listener.onRetreat ();
        stopBattle ();
      }
    });
  }

  @Override
  protected void addKeys ()
  {
    addKey (Input.Keys.ESCAPE, PopupAction.HIDE, new KeyListener ()
    {
      @Override
      public void keyDown ()
      {
        quitButton.toggle ();
      }
    });
  }

  @Override
  protected void setDiceTouchable (final boolean areTouchable)
  {
    setAttackerDiceTouchable (areTouchable);
    setDefenderDiceTouchable (false);
  }
}
