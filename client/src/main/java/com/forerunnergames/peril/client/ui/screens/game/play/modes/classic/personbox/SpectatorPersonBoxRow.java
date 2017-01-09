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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox;

import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.personicons.PersonIconWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.personicons.spectators.SpectatorIcon;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Arguments;

public final class SpectatorPersonBoxRow extends PersonBoxRow <SpectatorIcon, SpectatorPacket>
{
  public SpectatorPersonBoxRow (final SpectatorPacket spectator,
                                final MessageBoxRowStyle rowStyle,
                                final PersonIconWidgetFactory <SpectatorIcon, SpectatorPacket> widgetFactory)
  {
    super (spectator, rowStyle, widgetFactory);
  }

  @Override
  protected String createMessageTextLeft (final SpectatorPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    return "";
  }

  @Override
  protected String createMessageTextRight (final SpectatorPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    return person.getName ();
  }

  @Override
  protected String createMessageText (final String messageTextLeft, final String messageTextRight)
  {
    Arguments.checkIsNotNull (messageTextLeft, "messageTextLeft");
    Arguments.checkIsNotNull (messageTextRight, "messageTextRight");

    return messageTextRight;
  }
}
