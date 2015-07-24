package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class SideBar extends Table
{
  private static final int SIDEBAR_INNER_PADDING_TOP = 20;
  private static final int SIDEBAR_INNER_PADDING_LEFT = 20;
  private static final int SIDEBAR_INNER_PADDING_RIGHT = 20;
  private static final int BUTTON_WIDTH = 40;
  private static final int BUTTON_HEIGHT = 40;
  private static final int VERTICAL_PADDING_BETWEEN_BUTTONS = 20;

  public SideBar (final ClassicModePlayScreenWidgetFactory widgetFactory, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    top ().padTop (SIDEBAR_INNER_PADDING_TOP).padLeft (SIDEBAR_INNER_PADDING_LEFT)
            .padRight (SIDEBAR_INNER_PADDING_RIGHT);

    add (widgetFactory.createSideBarIcon (IconType.TRADE_IN, new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
      }
    })).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    row ().padTop (VERTICAL_PADDING_BETWEEN_BUTTONS);

    add (widgetFactory.createSideBarIcon (IconType.REINFORCE, new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
      }
    })).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    row ().padTop (VERTICAL_PADDING_BETWEEN_BUTTONS);

    add (widgetFactory.createSideBarIcon (IconType.END_TURN, new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
      }
    })).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    row ().padTop (VERTICAL_PADDING_BETWEEN_BUTTONS);

    add (widgetFactory.createSideBarIcon (IconType.MY_SETTINGS, new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
      }
    })).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);
  }

  public enum IconType
  {
    TRADE_IN ("trade-in"),
    REINFORCE ("reinforce"),
    END_TURN ("end-turn"),
    MY_SETTINGS ("my-settings");

    private final String styleName;

    IconType (final String styleName)
    {
      this.styleName = styleName;
    }

    public String getStyleName ()
    {
      return styleName;
    }
  }
}
