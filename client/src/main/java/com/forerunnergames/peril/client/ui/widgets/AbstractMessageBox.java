package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public abstract class AbstractMessageBox <T extends Message> extends ScrollPane implements MessageBox <T>
{
  private final Table table;
  private final RowStyle rowStyle;

  protected AbstractMessageBox (final ScrollPaneStyle scrollPaneStyle, final RowStyle rowStyle)
  {
    super (null, scrollPaneStyle);

    Arguments.checkIsNotNull (rowStyle, "rowStyle");

    this.rowStyle = rowStyle;

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

    table.row ().expandX ().fillX ().prefHeight (rowStyle.getHeight ());
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
  public void clear ()
  {
    clearTable ();
    configureTable ();
  }

  @Override
  public Actor asActor ()
  {
    return this;
  }

  protected abstract Actor createRow (final T message);

  private void clearTable ()
  {
    table.reset ();
  }

  private void configureTable ()
  {
    table.top ().padLeft (rowStyle.getPaddingLeft ()).padRight (rowStyle.getPaddingRight ());
  }
}
