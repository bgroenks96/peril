package com.forerunnergames.peril.common.net.events.ipc.interfaces;

import com.forerunnergames.tools.net.events.remote.origin.client.ClientEvent;

/**
 * Parent type for any IPC event that originates from the client module (not the IPC local networking client).
 */
public interface ClientInterProcessEvent extends InterProcessEvent, ClientEvent
{
}
