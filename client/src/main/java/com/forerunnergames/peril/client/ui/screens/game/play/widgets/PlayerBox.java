package com.forerunnergames.peril.client.ui.screens.game.play.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;

public final class PlayerBox extends ScrollPane
{
  private final Table table;
  private final Label.LabelStyle labelStyle;

  public PlayerBox (final ScrollPaneStyle scrollPaneStyle, final Label.LabelStyle labelStyle)
  {
    super (null, scrollPaneStyle);

    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    this.labelStyle = labelStyle;

    table = new Table ().top ().padLeft (8).padRight (8);

    setWidget (table);
    setOverscroll (false, false);
    setForceScroll (false, true);
    setFadeScrollBars (false);
    setScrollingDisabled (true, false);
    setScrollBarPositions (true, true);
    setScrollbarsOnTop (false);
    setSmoothScrolling (true);
  }

  public void addText (final String text)
  {
    table.row ().expandX ().fillX ().prefHeight (22);
    table.add (createLabel (text));
    table.layout ();

    layout ();
  }

  public void clear ()
  {
    table.reset ();
    table.top ().padLeft (8).padRight (8);
  }

  private Label createLabel (final String text)
  {
    final Label label = new Label (text, labelStyle);

    label.setWrap (true);

    return label;
  }
}
