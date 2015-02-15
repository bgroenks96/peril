package com.forerunnergames.peril.client.ui.screens.game.play.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;

public final class StatusBox extends ScrollPane
{
  private final Table statusBoxScrollTable;
  private final Label.LabelStyle labelStyle;

  public StatusBox (final ScrollPaneStyle scrollPaneStyle, final Label.LabelStyle labelStyle)
  {
    super (null, scrollPaneStyle);

    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    this.labelStyle = labelStyle;

    statusBoxScrollTable = new Table ().top ().padLeft (8).padRight (8);

    setWidget (statusBoxScrollTable);
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
    statusBoxScrollTable.row ().expandX ().fillX ().prefHeight (22);
    statusBoxScrollTable.add (createLabel (text));
    statusBoxScrollTable.layout ();

    layout ();
    setScrollY (getMaxY ());
  }

  public void clear ()
  {
    statusBoxScrollTable.reset ();
    statusBoxScrollTable.top ().padLeft (8).padRight (8);
  }

  private Label createLabel (final String text)
  {
    final Label label = new Label (text, labelStyle);

    label.setWrap (true);

    return label;
  }
}
