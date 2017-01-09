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

package com.forerunnergames.peril.common.net.dispatchers;

import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

/**
 * Resolves the the runtime type of events received from remote clients for interested parties, using some dispatch
 * resolving mechanism.
 */
public interface NetworkEventDispatcher
{
  /**
   * Call before {@link #dispatch(Event, RemoteClient)} to prepare the dispatch resolving mechanism.
   *
   * @see #dispatch(Event, RemoteClient)
   */
  void initialize ();

  /**
   * Resolves the the runtime type of the specified event received from the specified remote client.
   */
  void dispatch (final Event event, final RemoteClient client);

  /**
   * Shuts down the dispatch resolving mechanism and clears any stored events previously dispatched.
   */
  void shutDown ();
}
