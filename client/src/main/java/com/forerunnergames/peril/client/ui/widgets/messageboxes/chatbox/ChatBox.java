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

package com.forerunnergames.peril.client.ui.widgets.messageboxes.chatbox;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.ui.widgets.Padding;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.ScrollbarStyle;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.messages.DefaultChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import java.util.regex.Pattern;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

public final class ChatBox extends DefaultMessageBox <ChatBoxRow>
{
  private static final int SCROLLPANE_HEIGHT = 226 - 2 - 2;
  private static final int SCROLLPANE_TEXTFIELD_VERTICAL_PADDING = 2;
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
                  final MessageBoxRowStyle rowStyle,
                  final String textFieldStyleName,
                  final MBassador <Event> eventBus)
  {
    super (widgetFactory, scrollPaneStyle, scrollbarStyle, rowStyle, new Padding (0, 0, 2, 3), new Padding (0, 0, 6, 6));

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (textFieldStyleName, "textFieldStyleName");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;
    this.textFieldStyleName = textFieldStyleName;

    textField = widgetFactory.createTextField (TEXTFIELD_MAX_CHARACTERS, TEXTFIELD_FILTER, textFieldStyleName);
    textField.addListener (new TextFieldInputListener (eventBus));

    // @formatter:off
    table = new Table ().top ();
    table.setBackground (widgetFactory.createChatBoxBackgroundDrawable ());
    table.add (super.asActor ()).expandX ().fillX ().height (SCROLLPANE_HEIGHT).padBottom (SCROLLPANE_TEXTFIELD_VERTICAL_PADDING);
    table.row ();
    table.add (textField).expandX ().fillX ().height (TEXTFIELD_HEIGHT);
    // @formatter:on
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

    table.setBackground (widgetFactory.createChatBoxBackgroundDrawable ());
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
