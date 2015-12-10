package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

final class DefendPopupDiceFactory extends AbstractDiceFactory
{
  DefendPopupDiceFactory (final WidgetFactory widgetFactory)
  {
    super (widgetFactory);
  }

  @Override
  protected boolean areAttackerDiceTouchable ()
  {
    return false;
  }

  @Override
  protected boolean areDefenderDiceTouchable ()
  {
    return true;
  }
}
