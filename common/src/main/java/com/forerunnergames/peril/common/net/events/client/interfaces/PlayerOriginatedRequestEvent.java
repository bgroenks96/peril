package com.forerunnergames.peril.common.net.events.client.interfaces;

import com.forerunnergames.tools.net.events.remote.origin.client.InformRequestEvent;

/**
 * Represents request events sent by a client to the server after successfully joining the game as a player. Should be
 * answered by a {@link com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent} or
 * {@link com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent}
 *
 * Note: This interface should NOT be used for any event implementing {@link InformRequestEvent} NOR
 * {@link com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent}, but only for unsolicited
 * requests from a player.
 */
public interface PlayerOriginatedRequestEvent extends PlayerRequestEvent
{
}
