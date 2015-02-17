package com.forerunnergames.peril.client.ui.screens.game.play.widgets;

import static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.ui.widgets.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.LabelFactory;
import com.forerunnergames.peril.client.ui.widgets.RowStyle;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import net.engio.mbassy.bus.MBassador;

public final class ChatBox extends DefaultMessageBox <ChatMessage>
{
  private final Table table;
  private final TextField textField;

  public ChatBox (final ScrollPane.ScrollPaneStyle scrollPaneStyle,
                  final LabelFactory labelFactory,
                  final RowStyle rowStyle,
                  final TextFieldStyle textFieldStyle,
                  final MBassador <Event> eventBus)
  {
    super (scrollPaneStyle, labelFactory, rowStyle);

    Arguments.checkIsNotNull (textFieldStyle, "textFieldStyle");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    textField = new TextField ("", textFieldStyle);

    textField.addListener (new TextFieldInputListener (eventBus));

    table = new Table ().top ();
    table.add (super.asActor ()).expandX ().fillX ().height (199).padBottom (2);
    table.row ();
    table.add (textField).expandX ().fillX ().height (26).padTop (5).padLeft (4).padRight (4);
  }

  @Override
  public Actor asActor ()
  {
    return table;
  }

  private final class TextFieldInputListener extends InputListener
  {
    private MBassador <Event> eventBus;

    public TextFieldInputListener (final MBassador <Event> eventBus)
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
