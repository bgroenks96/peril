package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SnapshotArray;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

public abstract class AbstractMessageBox <T extends Message> implements MessageBox <T>
{
  private static final int MAX_ROWS = 40;
  private static final int V_SCROLLBAR_WIDTH = 14;
  private static final int SCROLLPANE_INNER_PADDING_TOP = 6;
  private final ScrollPane scrollPane;
  private final Table table;
  private final MessageBoxRowStyle messageBoxRowStyle;

  protected AbstractMessageBox (final ScrollPane.ScrollPaneStyle scrollPaneStyle,
                                final MessageBoxRowStyle messageBoxRowStyle,
                                final Scrollbars scrollbars)
  {
    Arguments.checkIsNotNull (messageBoxRowStyle, "messageBoxRowStyle");

    this.messageBoxRowStyle = messageBoxRowStyle;

    table = new Table ();
    configureTable ();

    scrollPane = new ScrollPane (table, scrollPaneStyle);
    scrollPane.setOverscroll (false, false);
    scrollPane.setFlickScroll (true);
    scrollPane.setForceScroll (false, scrollbars != Scrollbars.OPTIONAL && scrollbars == Scrollbars.REQUIRED);
    scrollPane.setFadeScrollBars (false);
    scrollPane.setScrollingDisabled (true, false);
    scrollPane.setScrollBarPositions (true, true);
    scrollPane.setScrollbarsOnTop (false);
    scrollPane.setSmoothScrolling (true);
    scrollPane.setVariableSizeKnobs (true);

    scrollPaneStyle.vScrollKnob.setMinWidth (V_SCROLLBAR_WIDTH);

    scrollPane.addListener (new InputListener ()
    {
      @Override
      public void enter (final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor)
      {
        if (scrollPane.getStage () == null) return;

        scrollPane.getStage ().setScrollFocus (scrollPane);
      }

      @Override
      public void exit (final InputEvent event, final float x, final float y, final int pointer, final Actor toActor)
      {
        if (scrollPane.getStage () == null) return;

        scrollPane.getStage ().setScrollFocus (null);
      }
    });
  }

  @Override
  public void addMessage (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    limitOldRows ();

    table.row ().expandX ().fillX ().prefHeight (messageBoxRowStyle.getHeight ());
    table.add (createRow (message));
    table.layout ();
    scrollPane.layout ();
  }

  @Override
  public void showLastMessage ()
  {
    scrollPane.setScrollY (scrollPane.getMaxY ());
  }

  @Override
  public void clear ()
  {
    table.reset ();
    configureTable ();
  }

  @Override
  public Actor asActor ()
  {
    return scrollPane;
  }

  protected abstract Actor createRow (final T message);

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
