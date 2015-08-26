package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public class DefaultMessageBox <T extends Message> extends AbstractMessageBox <T>
{
  private final WidgetFactory widgetFactory;

  public DefaultMessageBox (final ScrollPane.ScrollPaneStyle scrollPaneStyle,
                            final WidgetFactory widgetFactory,
                            final MessageBoxRowStyle messageBoxRowStyle,
                            final Scrollbars scrollbars)
  {
    super (scrollPaneStyle, messageBoxRowStyle, scrollbars);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;
  }

  @Override
  protected Actor createRow (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    return widgetFactory.createWrappingLabel (message.getText (), Align.left, "chat-and-status-message-text");
  }
}
