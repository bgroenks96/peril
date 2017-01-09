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

package com.forerunnergames.peril.common.eventbus;

import net.engio.mbassy.bus.common.PublicationEvent;

/**
 * Receives both {@link net.engio.mbassy.bus.common.DeadMessage} & {@link net.engio.mbassy.bus.common.FilteredMessage}
 */
public interface UnhandledEventHandler
{
  void onUnhandledEvent (final PublicationEvent unhandledEvent);
}
