package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.forerunnergames.tools.common.Arguments;

public final class SideBarButton
{
  private final ImageButton button;
  private final ButtonType type;

  public SideBarButton (final ImageButton button, final ButtonType type)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (type, "type");

    this.button = button;
    this.type = type;
  }

  public enum ButtonType
  {
    TRADE_IN ("trade-in"),
    REINFORCE ("reinforce"),
    END_TURN ("end-turn"),
    MY_SETTINGS ("my-settings");

    private final String styleName;

    ButtonType (final String styleName)
    {
      this.styleName = styleName;
    }

    public String getStyleName ()
    {
      return styleName;
    }
  }

  public Actor asActor ()
  {
    return button;
  }

  public ButtonType getType ()
  {
    return type;
  }

  public void setStyle (final ImageButton.ImageButtonStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    button.setStyle (style);
  }
}
