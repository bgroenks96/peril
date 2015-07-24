package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SnapshotArray;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Maths;
import com.forerunnergames.tools.common.Message;

public abstract class AbstractMessageBox <T extends Message> extends ScrollPane implements MessageBox <T>
{
  private static final int MAX_ROWS = 40;
  private static final int V_SCROLLBAR_WIDTH = 14;
  private static final int SCROLLPANE_INNER_PADDING_TOP = 6;
  private final Table table;
  private final MessageBoxRowStyle messageBoxRowStyle;

  protected AbstractMessageBox (final ScrollPaneStyle scrollPaneStyle, final MessageBoxRowStyle messageBoxRowStyle)
  {
    super (null, scrollPaneStyle);

    Arguments.checkIsNotNull (messageBoxRowStyle, "messageBoxRowStyle");

    this.messageBoxRowStyle = messageBoxRowStyle;

    table = new Table ();
    configureTable ();

    setWidget (table);
    setOverscroll (false, false);
    setFlickScroll (true);
    setForceScroll (false, true);
    setFadeScrollBars (false);
    setScrollingDisabled (true, false);
    setScrollBarPositions (true, true);
    setScrollbarsOnTop (false);
    setSmoothScrolling (true);
    setVariableSizeKnobs (true);

    scrollPaneStyle.vScrollKnob.setMinWidth (V_SCROLLBAR_WIDTH);

    // @formatter:off
    addListener (new InputListener ()
    {
      @Override
      public void enter (final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor)
      {
        getStage ().setScrollFocus (AbstractMessageBox.this);
      }

      @Override
      public void exit (final InputEvent event, final float x, final float y, final int pointer, final Actor toActor)
      {
        getStage ().setScrollFocus (null);
      }
    });
    // @formatter:on
  }

  @Override
  public void addMessage (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    limitOldRows ();

    final Actor row = createRow (message);
    table.row ().expandX ().fillX ().prefHeight (messageBoxRowStyle.getHeight ());
    final Cell <Actor> messageCell = table.add (row);

    table.layout ();
    layout ();
    messageCell.height (Maths.nextHigherMultiple (Math.round (row.getHeight ()),
                                                  Math.round (messageBoxRowStyle.getHeight ())));
    table.invalidateHierarchy ();
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

  private void limitOldRows ()
  {
    if (table.getRows () < MAX_ROWS) return;

    final SnapshotArray <Actor> children = table.getChildren ();
    children.ordered = false;

    for (int i = 0; i < children.size - 1; ++i)
    {
      children.swap (i, i + 1);
    }

    table.removeActor (children.get (children.size - 1));
    table.getCells ().removeIndex (0);
  }
}
