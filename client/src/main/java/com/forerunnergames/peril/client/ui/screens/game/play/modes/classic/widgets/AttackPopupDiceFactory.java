package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

final class AttackPopupDiceFactory extends AbstractDiceFactory
{
  AttackPopupDiceFactory (final WidgetFactory widgetFactory)
  {
    super (widgetFactory);
  }

  @Override
  protected boolean areAttackerDiceTouchable ()
  {
    return true;
  }

  @Override
  protected boolean areDefenderDiceTouchable ()
  {
    return false;
  }
}
