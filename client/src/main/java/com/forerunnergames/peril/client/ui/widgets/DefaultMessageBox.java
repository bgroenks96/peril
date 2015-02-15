package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public class DefaultMessageBox <T extends Message> extends AbstractMessageBox <T>
{
  private final Label.LabelStyle labelStyle;

  public DefaultMessageBox (final ScrollPaneStyle scrollPaneStyle,
                            final Label.LabelStyle labelStyle,
                            final RowStyle rowStyle)
  {
    super (scrollPaneStyle, rowStyle);

    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    this.labelStyle = labelStyle;
  }

  @Override
  protected Actor createRow (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    return createLabel (message.getText ());
  }

  private Label createLabel (final String text)
  {
    final Label label = new Label (text, labelStyle);

    label.setWrap (true);

    return label;
  }
}
