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
import com.forerunnergames.peril.client.ui.widgets.MessageBoxRowStyle;
import com.forerunnergames.peril.core.shared.net.events.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import net.engio.mbassy.bus.MBassador;

public final class ChatBox extends DefaultMessageBox <ChatMessage>
{
  private static final int SCROLLPANE_HEIGHT = 226 - 2 - 2;
  private static final int SCROLLPANE_TEXTFIELD_VERTICAL_PADDING = 2 + 2;
  private static final int TEXTFIELD_HEIGHT = 24;
  private static final int TEXTFIELD_INNER_PADDING_LEFT = 12;
  private static final int TEXTFIELD_INNER_PADDING_RIGHT = 14;
  private final Table table;
  private final TextField textField;

  public ChatBox (final ScrollPane.ScrollPaneStyle scrollPaneStyle,
                  final LabelFactory labelFactory,
                  final MessageBoxRowStyle messageBoxRowStyle,
                  final TextFieldStyle textFieldStyle,
                  final MBassador <Event> eventBus)
  {
    super (scrollPaneStyle, labelFactory, messageBoxRowStyle);

    Arguments.checkIsNotNull (textFieldStyle, "textFieldStyle");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    textField = new TextField ("", textFieldStyle);

    textField.addListener (new TextFieldInputListener (eventBus));

    table = new Table ().top ();
    table.add (super.asActor ()).expandX ().fillX ().height (SCROLLPANE_HEIGHT)
        .padBottom (SCROLLPANE_TEXTFIELD_VERTICAL_PADDING);
    table.row ();
    table.add (textField).expandX ().fillX ().height (TEXTFIELD_HEIGHT).padLeft (TEXTFIELD_INNER_PADDING_LEFT)
        .padRight (TEXTFIELD_INNER_PADDING_RIGHT);
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
          eventBus.publish (new ChatMessageRequestEvent (new DefaultChatMessage (textFieldText)));
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
