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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.common.net.events.interfaces.KickEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

public final class KickPlayerFromGameSuccessEvent implements KickEvent, SuccessEvent
{
  private final KickEvent kickEvent;

  public KickPlayerFromGameSuccessEvent (final String reasonForKick)
  {
    Arguments.checkIsNotNull (reasonForKick, "reasonForKick");

    kickEvent = new DefaultKickEvent (reasonForKick);
  }

  @Override
  public String getReasonForKick ()
  {
    return kickEvent.getReasonForKick ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: | %2$s", getClass ().getSimpleName (), kickEvent);
  }

  @RequiredForNetworkSerialization
  private KickPlayerFromGameSuccessEvent ()
  {
    kickEvent = null;
  }
}
