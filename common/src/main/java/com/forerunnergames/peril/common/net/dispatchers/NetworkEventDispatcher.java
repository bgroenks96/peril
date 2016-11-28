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
