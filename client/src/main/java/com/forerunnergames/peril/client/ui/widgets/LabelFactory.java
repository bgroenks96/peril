package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.forerunnergames.tools.common.Arguments;

public final class LabelFactory
{
  private final Label.LabelStyle labelStyle;

  public LabelFactory (final Label.LabelStyle labelStyle)
  {
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    this.labelStyle = labelStyle;
  }

  public Label create (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    final Label label = new Label (text, labelStyle);

    label.setWrap (true);

    return label;
  }
}
