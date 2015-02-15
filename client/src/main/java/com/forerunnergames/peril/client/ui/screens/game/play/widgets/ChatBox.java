package com.forerunnergames.peril.client.ui.screens.game.play.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import net.engio.mbassy.bus.MBassador;

public final class ChatBox extends Table
{
  private final Table table;
  private final ScrollPane scrollPane;
  private final Label.LabelStyle labelStyle;
  private final TextField textField;

  public ChatBox (final ScrollPane.ScrollPaneStyle scrollPaneStyle,
                  final Label.LabelStyle labelStyle,
                  final TextField.TextFieldStyle textFieldStyle,
                  final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (scrollPaneStyle, "scrollPaneStyle");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");
    Arguments.checkIsNotNull (textFieldStyle, "textFieldStyle");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.labelStyle = labelStyle;

    table = new Table ().top ().padLeft (8).padRight (8);

    scrollPane = new ScrollPane (table, scrollPaneStyle);
    scrollPane.setOverscroll (false, false);
    scrollPane.setForceScroll (false, true);
    scrollPane.setFadeScrollBars (false);
    scrollPane.setScrollingDisabled (true, false);
    scrollPane.setScrollBarPositions (true, true);
    scrollPane.setScrollbarsOnTop (false);
    scrollPane.setSmoothScrolling (true);

    textField = new TextField ("", textFieldStyle);
    textField.addListener (new TextFieldInputListener (eventBus));

    top ();
    add (scrollPane).expandX ().fillX ().height (199).padBottom (2);
    row ();
    add (textField).expandX ().fillX ().height (26).padTop (5).padLeft (4).padRight (4);
  }

  public void addText (final String text)
  {
    table.row ().expandX ().fillX ().prefHeight (22);
    table.add (createLabel (text));
    table.layout ();

    scrollPane.layout ();
    scrollPane.setScrollY (scrollPane.getMaxY ());
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

  private final class TextFieldInputListener extends InputListener
  {
    private MBassador <Event> eventBus;

    private TextFieldInputListener (final MBassador <Event> eventBus)
    {
      Arguments.checkIsNotNull (eventBus, "eventBus");

      this.eventBus = eventBus;
    }

    @Override
    public boolean keyDown (final InputEvent event, final int keycode)
    {
      switch (keycode)
      {
        case Input.Keys.ENTER:
        {
          final String textFieldText = Strings.compressWhitespace (textField.getText ().trim ());

          textField.setText ("");

          if (Strings.isPrintable (textFieldText))
          {
            eventBus.publish (new DefaultChatMessageEvent (new DefaultChatMessage (textFieldText)));
          }

          return true;
        }
        default:
        {
          return false;
        }
      }
    }
  }
}
