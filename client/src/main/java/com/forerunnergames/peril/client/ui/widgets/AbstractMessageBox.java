package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public abstract class AbstractMessageBox <T extends Message> extends ScrollPane implements MessageBox <T>
{
  private static final int SCROLLPANE_INNER_PADDING_TOP = 6;
  private final Table table;
  private final MessageBoxRowStyle messageBoxRowStyle;

  protected AbstractMessageBox (final ScrollPaneStyle scrollPaneStyle, final MessageBoxRowStyle messageBoxRowStyle)
  {
    super (null, scrollPaneStyle);

    Arguments.checkIsNotNull (messageBoxRowStyle, "rowStyle");

    this.messageBoxRowStyle = messageBoxRowStyle;

    table = new Table ();
    configureTable ();

    setWidget (table);
    setOverscroll (false, false);
    setForceScroll (false, true);
    setFadeScrollBars (false);
    setScrollingDisabled (true, false);
    setScrollBarPositions (true, true);
    setScrollbarsOnTop (false);
    setSmoothScrolling (true);
  }

  @Override
  public void addMessage (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    table.row ().expandX ().fillX ().prefHeight (messageBoxRowStyle.getHeight ());
    table.add (createRow (message));
    table.layout ();

    layout ();
  }

  @Override
  public void showLastMessage ()
  {
    setScrollY (getMaxY ());
  }

  @Override
  public Actor asActor ()
  {
    return this;
  }

  @Override
  public void clear ()
  {
    clearTable ();
    configureTable ();
  }

  protected abstract Actor createRow (final T message);

  private void clearTable ()
  {
    table.reset ();
  }

  private void configureTable ()
  {
    table.top ().padLeft (messageBoxRowStyle.getPaddingLeft ()).padRight (messageBoxRowStyle.getPaddingRight ())
        .padTop (SCROLLPANE_INNER_PADDING_TOP);
  }
}
