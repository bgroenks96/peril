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

package com.forerunnergames.peril.common.net.events.defaults;

import com.forerunnergames.peril.common.net.events.interfaces.MessageEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractMessageEvent <T extends Message> implements MessageEvent <T>
{
  private final T message;

  public AbstractMessageEvent (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    this.message = message;
  }

  @RequiredForNetworkSerialization
  protected AbstractMessageEvent ()
  {
    message = null;
  }

  @Override
  public final T getMessage ()
  {
    return message;
  }

  @Override
  public final String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Message: {}", getClass ().getSimpleName (), message.getText ());
  }
}
