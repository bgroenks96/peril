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

package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerRetreatDeniedEvent.Reason;

/**
 * Note: This event is currently never sent by Core, since retreats are always allowed while the inform event is valid.
 */
public class PlayerRetreatDeniedEvent extends AbstractPlayerDeniedEvent <PlayerRetreatRequestEvent, Reason>
{
  public enum Reason
  {
    NOT_IN_TURN
  }
}