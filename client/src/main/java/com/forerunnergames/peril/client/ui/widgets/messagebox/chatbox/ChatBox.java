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

package com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.DefaultMessageBox;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.messages.DefaultChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

public final class ChatBox extends DefaultMessageBox <ChatBoxRow>
{
  private final WidgetFactory widgetFactory;
  private final ChatBoxStyle style;
  private final Table table;
  private final TextField textField;

  public ChatBox (final ChatBoxStyle style, final WidgetFactory widgetFactory, final MBassador <Event> eventBus)
  {
    super (style, widgetFactory);

    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.style = style;
    this.widgetFactory = widgetFactory;

    textField = widgetFactory.createTextField (style.getTextFieldMaxChars (), style.getTextFieldFilter (),
                                               style.getTextFieldStyleName ());
    textField.addListener (new TextFieldInputListener (eventBus));

    // @formatter:off
    table = new Table ().top ();
    table.setBackground (widgetFactory.createChatBoxBackgroundDrawable ());
    table.add (super.asActor ()).expandX ().fillX ().height (style.getScrollPaneHeight ()).padBottom (style.getScrollPaneTextFieldSpacing ());
    table.row ();
    table.add (textField).expandX ().fillX ().height (style.getTextFieldHeight ());
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
    textField.setStyle (widgetFactory.createTextFieldStyle (style.getTextFieldStyleName ()));
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Style: [{}] | TextField: [{}] | Container: [{}] | WidgetFactory: [{}]",
                           super.toString (), style, textField, table, widgetFactory);
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
