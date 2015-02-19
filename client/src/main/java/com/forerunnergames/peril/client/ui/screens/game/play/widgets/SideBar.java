package com.forerunnergames.peril.client.ui.screens.game.play.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenWidgetFactory;
import com.forerunnergames.tools.common.Arguments;

public final class SideBar extends Table
{
  private static final int SIDEBAR_INNER_PADDING_TOP = 20;
  private static final int SIDEBAR_INNER_PADDING_LEFT = 20;
  private static final int SIDEBAR_INNER_PADDING_RIGHT = 20;
  private static final int BUTTON_ROW_COUNT = 12;
  private static final int BUTTON_WIDTH = 40;
  private static final int BUTTON_HEIGHT = 40;
  private static final int VERTICAL_PADDING_BETWEEN_BUTTONS = 20;

  public SideBar (final PlayScreenWidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    top ().padTop (SIDEBAR_INNER_PADDING_TOP).padLeft (SIDEBAR_INNER_PADDING_LEFT)
        .padRight (SIDEBAR_INNER_PADDING_RIGHT);

    add (widgetFactory.createButton ()).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    for (int i = 0; i < BUTTON_ROW_COUNT - 1; ++i)
    {
      row ().padTop (VERTICAL_PADDING_BETWEEN_BUTTONS);
      add (widgetFactory.createButton ()).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);
    }
  }
}
