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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SnapshotArray;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class DefaultMessageBox <T extends Message> implements MessageBox <T>
{
  private static final int MAX_ROWS = 40;
  private static final int SCROLLPANE_INNER_PADDING_TOP = 6;
  private final ScrollPane scrollPane;
  private final Table table;
  private final WidgetFactory widgetFactory;
  private final String scrollPaneStyleName;
  private final ScrollbarStyle scrollbarStyle;
  private final MessageBoxRowStyle messageBoxRowStyle;

  public DefaultMessageBox (final WidgetFactory widgetFactory,
                            final String scrollPaneStyleName,
                            final ScrollbarStyle scrollbarStyle,
                            final MessageBoxRowStyle messageBoxRowStyle)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (scrollPaneStyleName, "scrollPaneStyleName");
    Arguments.checkIsNotNull (scrollbarStyle, "scrollBarStyle");
    Arguments.checkIsNotNull (messageBoxRowStyle, "messageBoxRowStyle");

    this.widgetFactory = widgetFactory;
    this.scrollPaneStyleName = scrollPaneStyleName;
    this.scrollbarStyle = scrollbarStyle;
    this.messageBoxRowStyle = messageBoxRowStyle;

    table = new Table ();
    configureTable ();

    scrollPane = new ScrollPane (table, widgetFactory.createScrollPaneStyle (scrollPaneStyleName, scrollbarStyle));
    scrollPane.setOverscroll (false, false);
    scrollPane.setFlickScroll (true);
    scrollPane.setForceScroll (false, scrollbarStyle.areScrollbarsRequired ());
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
  public void addMessage (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    limitOldRows ();

    table.row ().expandX ().fillX ().prefHeight (messageBoxRowStyle.getHeight ());
    table.add (widgetFactory.createWrappingLabel (message.getText (), messageBoxRowStyle.getLabelAlignment (),
                                                  messageBoxRowStyle.getLabelStyleName ()));
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

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    final ScrollPane.ScrollPaneStyle scrollPaneStyle = widgetFactory.createScrollPaneStyle (scrollPaneStyleName,
                                                                                            scrollbarStyle);
    scrollPane.setStyle (scrollPaneStyle);

    for (final Actor actor : table.getChildren ())
    {
      if (!(actor instanceof Label)) continue;

      ((Label) actor).setStyle (widgetFactory.createLabelStyle (messageBoxRowStyle.getLabelStyleName ()));
    }
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
