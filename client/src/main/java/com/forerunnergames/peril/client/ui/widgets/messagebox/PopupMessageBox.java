package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public abstract class PopupMessageBox <T extends Message> extends AbstractMessageBox <T>
{
  private static final int MESSAGE_BOX_ROW_HEIGHT = 24;
  private static final int MESSAGE_BOX_ROW_PADDING_LEFT = 12;
  private static final int MESSAGE_BOX_ROW_PADDING_RIGHT = 12;

  public PopupMessageBox (final Skin skin)
  {
    super (skin.get (ScrollPane.ScrollPaneStyle.class),
           new MessageBoxRowStyle (MESSAGE_BOX_ROW_HEIGHT, MESSAGE_BOX_ROW_PADDING_LEFT, MESSAGE_BOX_ROW_PADDING_RIGHT),
           Scrollbars.OPTIONAL);
  }

  @Override
  protected Actor createRow (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    return createMessageLabel (message.getText ());
  }

  protected abstract Label createMessageLabel (final String message);
}
