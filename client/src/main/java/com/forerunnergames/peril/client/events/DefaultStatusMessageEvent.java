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

package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

public final class DefaultStatusMessageEvent implements StatusMessageEvent
{
  private final StatusMessage message;
  private final ImmutableSet <PlayerPacket> recipients;

  public DefaultStatusMessageEvent (final StatusMessage message, final ImmutableSet <PlayerPacket> recipients)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (recipients, "recipients");

    this.message = message;
    this.recipients = recipients;
  }

  @Override
  public StatusMessage getMessage ()
  {
    return message;
  }

  @Override
  public String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  public ImmutableSet <PlayerPacket> getRecipients ()
  {
    return recipients;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Message: {} | Recipients: {}", getClass ().getSimpleName (), message, recipients);
  }
}
