/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.OkDialog;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

public final class PlayerNotificationDialog extends OkDialog
{
  @Nullable
  private PlayerPacket selfPlayer;

  public PlayerNotificationDialog (final WidgetFactory widgetFactory, final Stage stage, final DialogListener listener)
  {
    // @formatter:off
    super (widgetFactory,
            DialogStyle.builder ()
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

  public void clearTitleForSelf (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    setTitleForSelf (player, "");
  }

  public void setTitleForSelf (final PlayerPacket player, final String title)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (title, "title");

    if (isSelf (player)) setTitle (title);
  }

  public void setTitleForSelf (final PlayerPacket player, final String title, final Object... titleArgs)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (title, "title");
    Arguments.checkIsNotNull (titleArgs, "titleArgs");

    setTitleForSelf (player, Strings.format (title, titleArgs));
  }

  public void setTitleForEveryoneElse (final PlayerPacket player, final String title)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (title, "title");

    if (!isSelf (player)) setTitle (title);
  }

  public void setTitleForEveryoneElse (final PlayerPacket player, final String title, final Object... titleArgs)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (title, "title");
    Arguments.checkIsNotNull (titleArgs, "titleArgs");

    setTitleForEveryoneElse (player, Strings.format (title, titleArgs));
  }

  public void showForSelf (final PlayerPacket player, final String message)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (message, "message");

    if (isSelf (player)) show (message);
  }

  public void showForSelf (final PlayerPacket player, final String message, final Object... messageArgs)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (messageArgs, "messageArgs");

    showForSelf (player, Strings.format (message, messageArgs));
  }

  public void showForEveryoneElse (final PlayerPacket player, final String message)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (message, "message");

    if (!isSelf (player)) show (message);
  }

  public void showForEveryoneElse (final PlayerPacket player, final String message, final Object... messageArgs)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (messageArgs, "messageArgs");

    showForEveryoneElse (player, Strings.format (message, messageArgs));
  }

  public void setSelfPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    selfPlayer = player;
  }

  private boolean isSelf (final PlayerPacket player)
  {
    assert player != null;

    return selfPlayer != null && player.is (selfPlayer);
  }
}
