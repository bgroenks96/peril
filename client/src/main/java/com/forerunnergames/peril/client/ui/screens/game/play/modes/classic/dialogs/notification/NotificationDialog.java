/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.notification;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.PlayMapBlockingDialog;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.OkDialog;
import com.forerunnergames.peril.common.net.packets.person.PersonPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

public final class NotificationDialog extends OkDialog implements PlayMapBlockingDialog
{
  @Nullable
  private PersonPacket self;

  public NotificationDialog (final WidgetFactory widgetFactory, final Stage stage, final DialogListener listener)
  {
    // @formatter:off
    super (widgetFactory,
            DialogStyle.builder ()
                    .modal (false)
                    .windowStyle (StyleSettings.PLAYER_NOTIFICATION_DIALOG_WINDOW_STYLE)
                    .size (650, 244)
                    .position (587, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 284)
                    .titleHeight (51)
                    .border (28)
                    .buttonSpacing (16)
                    .buttonWidth (90)
                    .textBoxPaddingHorizontal (2)
                    .textBoxPaddingBottom (21)
                    .textPaddingHorizontal (4)
                    .textPaddingBottom (4)
                    .movable (true)
                    .build (),
            stage, listener);
    // @formatter:on
  }

  public void clearTitleForSelf (final PersonPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    setTitleForSelf (person, "");
  }

  public void setTitleForSelf (final PersonPacket person, final String title)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (title, "title");

    if (isSelf (person)) setTitle (title);
  }

  public void setTitleForSelf (final PersonPacket person, final String title, final Object... titleArgs)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (title, "title");
    Arguments.checkIsNotNull (titleArgs, "titleArgs");

    setTitleForSelf (person, Strings.format (title, titleArgs));
  }

  public void setTitleForEveryoneElse (final PersonPacket person, final String title)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (title, "title");

    if (!isSelf (person)) setTitle (title);
  }

  public void setTitleForEveryoneElse (final PersonPacket person, final String title, final Object... titleArgs)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (title, "title");
    Arguments.checkIsNotNull (titleArgs, "titleArgs");

    setTitleForEveryoneElse (person, Strings.format (title, titleArgs));
  }

  public void showForSelf (final PersonPacket person, final String message)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (message, "message");

    if (isSelf (person)) show (message);
  }

  public void showForSelf (final PersonPacket person, final String message, final Object... messageArgs)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (messageArgs, "messageArgs");

    showForSelf (person, Strings.format (message, messageArgs));
  }

  public void showForEveryoneElse (final PersonPacket person, final String message)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (message, "message");

    if (!isSelf (person)) show (message);
  }

  public void showForEveryoneElse (final PersonPacket person, final String message, final Object... messageArgs)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (messageArgs, "messageArgs");

    showForEveryoneElse (person, Strings.format (message, messageArgs));
  }

  public void setSelf (final PersonPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    self = person;
  }

  private boolean isSelf (final PersonPacket person)
  {
    assert person != null;

    return self != null && person.is (self);
  }
}
