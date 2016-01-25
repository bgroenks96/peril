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

package com.forerunnergames.peril.client.assets;

import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.common.eventbus.DeadEventHandler;
import com.forerunnergames.tools.common.Arguments;

import net.engio.mbassy.bus.common.DeadMessage;

public class AssetLoadingErrorDeadEventHandler implements DeadEventHandler
{
  @Override
  public void onDeadMessage (final DeadMessage deadMessage)
  {
    Arguments.checkIsNotNull (deadMessage, "deadMessage");

    if (!(deadMessage.getMessage () instanceof AssetLoadingErrorEvent)) return;

    throw new RuntimeException (((AssetLoadingErrorEvent) deadMessage.getMessage ()).getThrowable ());
  }
}
