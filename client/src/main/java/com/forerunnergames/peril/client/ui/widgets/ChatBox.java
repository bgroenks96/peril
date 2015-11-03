package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.ui.widgets.messagebox.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.messagebox.ScrollbarStyle;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.peril.common.net.messages.DefaultChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import java.util.regex.Pattern;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

public final class ChatBox extends DefaultMessageBox <ChatMessage>
{
  private static final int SCROLLPANE_HEIGHT = 226 - 2 - 2;
  private static final int SCROLLPANE_TEXTFIELD_VERTICAL_PADDING = 2 + 2;
  private static final int TEXTFIELD_MAX_CHARACTERS = 80;
  private static final int TEXTFIELD_HEIGHT = 24;
  private static final Pattern TEXTFIELD_FILTER = Pattern.compile (".*");
  private final WidgetFactory widgetFactory;
  private final Table table;
  private final TextField textField;
  private final String textFieldStyleName;

  public ChatBox (final WidgetFactory widgetFactory,
                  final String scrollPaneStyle,
                  final ScrollbarStyle scrollbarStyle,
                  final MessageBoxRowStyle messageBoxRowStyle,
                  final String textFieldStyleName,
                  final MBassador <Event> eventBus)
  {
    super (widgetFactory, scrollPaneStyle, scrollbarStyle, messageBoxRowStyle);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (textFieldStyleName, "textFieldStyleName");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;
    this.textFieldStyleName = textFieldStyleName;

    textField = widgetFactory.createTextField (TEXTFIELD_MAX_CHARACTERS, TEXTFIELD_FILTER, textFieldStyleName);
    textField.addListener (new TextFieldInputListener (eventBus));

    table = new Table ().top ();
    table.add (super.asActor ()).expandX ().fillX ().height (SCROLLPANE_HEIGHT)
            .padBottom (SCROLLPANE_TEXTFIELD_VERTICAL_PADDING);
    table.row ();
    table.add (textField).expandX ().fillX ().height (TEXTFIELD_HEIGHT);
  }

  @Override
  public void clear ()
  {
    super.clear ();

    textField.setText ("");
  }

  @Override
  public Actor asActor ()
  {
    return table;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    super.refreshAssets ();

    textField.setStyle (widgetFactory.createTextFieldStyle (textFieldStyleName));
  }

  private final class TextFieldInputListener extends InputListener
  {
    private final MBassador <Event> eventBus;

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

    private TextFieldInputListener (final MBassador <Event> eventBus)
    {
      Arguments.checkIsNotNull (eventBus, "eventBus");

      this.eventBus = eventBus;
    }
  }
}
