package com.forerunnergames.peril.common.net.events.ipc.interfaces;

import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

/**
 * Parent type for any IPC event that originates from the server module (not the IPC local networking server).
 */
public interface ServerInterProcessEvent extends InterProcessEvent, ServerEvent
{

}
