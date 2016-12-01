/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMessageBox <T extends MessageBoxRow <? extends Message>> implements MessageBox <T>
{
  private static final Logger log = LoggerFactory.getLogger (DefaultMessageBox.class);
  private static final int MAX_ROWS = 100;
  private final Map <Actor, T> rowCache = new LinkedHashMap<> ();
  private final MessageBoxStyle style;
  private final WidgetFactory widgetFactory;
  private final ScrollPane scrollPane;
  private final Table table;

  public DefaultMessageBox (final MessageBoxStyle style, final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.style = style;
    this.widgetFactory = widgetFactory;

    table = new Table ();

    configureTable ();

    scrollPane = new ScrollPane (table,
            widgetFactory.createScrollPaneStyle (style.getScrollPaneStyle (), style.getScrollbarStyle ()))
    {
      private final int scissorsDeltaY = 2 + DefaultMessageBox.this.style.getScrollPaddingBottom ();
      private final int scissorsDeltaHeight = 1 - DefaultMessageBox.this.style.getScrollPaddingTop ()
              - DefaultMessageBox.this.style.getScrollPaddingBottom ();

      @Override
      protected void drawChildren (final Batch batch, final float parentAlpha)
      {
        final Rectangle scissors = ScissorStack.popScissors ();
        scissors.setY (scissors.getY () + scissorsDeltaY);
        scissors.setHeight (scissors.getHeight () + scissorsDeltaHeight);
        ScissorStack.pushScissors (scissors);
        super.drawChildren (batch, parentAlpha);
      }
    };

    scrollPane.setOverscroll (false, false);
    scrollPane.setFlickScroll (true);
    scrollPane.setForceScroll (false, style.areScrollbarsRequired ());
    scrollPane.setFadeScrollBars (false);
    scrollPane.setScrollingDisabled (true, false);
    scrollPane.setScrollBarPositions (true, true);
    scrollPane.setScrollbarsOnTop (false);
    scrollPane.setSmoothScrolling (true);
    scrollPane.setVariableSizeKnobs (true);

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
  public void addRow (final T row)
  {
    Arguments.checkIsNotNull (row, "row");

    limitOldRows ();

    table.row ().expandX ().fillX ().prefHeight (style.getRowHeight ());
    table.add (row.asActor ());

    table.layout ();
    scrollPane.layout ();

    rowCache.put (row.asActor (), row);
  }

  @Override
  public void showLastRow ()
  {
    scrollPane.setScrollY (scrollPane.getMaxY ());
  }

  @Override
  public void clear ()
  {
    rowCache.clear ();
    table.reset ();
    configureTable ();
  }

  @Override
  public MessageBoxRowStyle getRowStyle ()
  {
    return style.getRowStyle ();
  }

  @Override
  public Actor asActor ()
  {
    return scrollPane;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    scrollPane.setStyle (widgetFactory.createScrollPaneStyle (style.getScrollPaneStyle (), style.getScrollbarStyle ()));

    for (final Actor actor : table.getChildren ())
    {
      final MessageBoxRow <?> row = rowCache.get (actor);

      if (row == null)
      {
        log.warn ("Not refreshing assets of actor [{}] because {} not found in cache.", actor,
                  MessageBoxRow.class.getSimpleName ());
        continue;
      }

      row.refreshAssets ();
    }
  }

  @Override
  public boolean hasRowWithIndex (final int index)
  {
    return index >= 0 && index < table.getCells ().size;
  }

  @Override
  public T getRowByIndex (final int index)
  {
    Arguments.checkIsTrue (hasRowWithIndex (index), "Row does not exist with index [{}].", index);

    final Cell <?> cell = table.getCells ().get (index);
    final Actor actor = cell.getActor ();

    if (actor == null)
    {
      throw new IllegalStateException (Strings.format ("Row with index [{}] is invalid (actor is null).", index));
    }

    final T row = rowCache.get (actor);

    if (row == null)
    {
      throw new IllegalStateException (Strings.format ("Row with index [{}] is invalid (row is null).", index));
    }

    return row;
  }

  @Override
  public ImmutableList <T> getRows ()
  {
    return ImmutableList.copyOf (rowCache.values ());
  }

  private void configureTable ()
  {
    table.top ().padLeft (style.getRowPaddingLeft ()).padRight (style.getRowPaddingRight ())
            .padTop (style.getAbsolutePaddingTop ()).padBottom (style.getAbsolutePaddingBottom ());
  }

  private void limitOldRows ()
  {
    if (rowCache.size () < MAX_ROWS) return;

    log.trace ("Limit old rows (before): Number of rows in cache: {}", getRows ().size ());
    log.trace ("Limit old rows (before): Number of rows in table: {}", table.getRows ());

    // Remove oldest row (safety is guaranteed as long as MAX_ROWS > 0).
    final Iterator <Map.Entry <Actor, T>> iterator = rowCache.entrySet ().iterator ();
    final Map.Entry <Actor, T> entryToRemove = iterator.next ();
    iterator.remove ();
    log.trace ("Removed old row [{}].", entryToRemove.getValue ());

    // Reset table
    table.clear ();
    configureTable ();

    // Re-add all rows (with the oldest one already removed).
    for (final Map.Entry <Actor, T> entry : rowCache.entrySet ())
    {
      table.row ().expandX ().fillX ().prefHeight (style.getRowHeight ());
      table.add (entry.getKey ());
    }

    table.layout ();
    scrollPane.layout ();

    log.trace ("Limit old rows (after): Number of rows in cache: {}", getRows ().size ());
    log.trace ("Limit old rows (after): Number of rows in table: {}", table.getRows ());
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Style: [{}] | ScrollPane: [{}] | ScrollPane Child Widget: [{}] | Row Cache: [{}] | "
                                   + "Max Rows: [{}] | WidgetFactory: [{}]",
                           getClass ().getSimpleName (), style, scrollPane, table, rowCache, MAX_ROWS, widgetFactory);
  }
}
