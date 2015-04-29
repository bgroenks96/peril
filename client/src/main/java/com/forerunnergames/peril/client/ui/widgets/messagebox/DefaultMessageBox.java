package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.widgets.LabelFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public class DefaultMessageBox <T extends Message> extends AbstractMessageBox <T>
{
  private final LabelFactory labelFactory;

  public DefaultMessageBox (final ScrollPaneStyle scrollPaneStyle,
                            final LabelFactory labelFactory,
                            final MessageBoxRowStyle messageBoxRowStyle)
  {
    super (scrollPaneStyle, messageBoxRowStyle);

    Arguments.checkIsNotNull (labelFactory, "labelFactory");

    this.labelFactory = labelFactory;
  }

  @Override
  protected Actor createRow (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    return labelFactory.create (message.getText ());
  }
}
