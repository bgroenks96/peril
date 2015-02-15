package com.forerunnergames.peril.client.ui.screens.game.play.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenWidgetFactory;
import com.forerunnergames.tools.common.Arguments;

public final class SideBar extends Table
{
  private static final int BUTTON_ROW_COUNT = 13;

  public SideBar (final PlayScreenWidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    top ().padTop (33).padBottom (33).padLeft (32).padRight (32);

    add (widgetFactory.createButton ()).top ().width (42).height (42);
    add (widgetFactory.createButton ()).top ().width (42).height (42).padLeft (26);

    for (int i = 0; i < BUTTON_ROW_COUNT - 1; ++i)
    {
      row ().padTop (16);
      add (widgetFactory.createButton ()).top ().width (42).height (42);
      add (widgetFactory.createButton ()).top ().width (42).height (42).padLeft (26);
    }
  }
}
