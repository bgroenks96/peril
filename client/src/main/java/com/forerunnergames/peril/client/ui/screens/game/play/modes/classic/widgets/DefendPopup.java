package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class DefendPopup extends AbstractBattlePopup
{
  public DefendPopup (final BattlePopupWidgetFactory widgetFactory,
                      final Stage stage,
                      final BattlePopupListener listener,
                      final MBassador <Event> eventBus)
  {
    super (widgetFactory, new DefendPopupDiceFactory (widgetFactory), "Defend", stage, listener, eventBus);
  }

  @Override
  protected void addButtons ()
  {
  }

  @Override
  protected void setDiceTouchable (final boolean areTouchable)
  {
    setAttackerDiceTouchable (false);
    setDefenderDiceTouchable (areTouchable);
  }
}
