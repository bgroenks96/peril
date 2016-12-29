package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.tools.net.events.remote.origin.server.ServerQuestionEvent;

/**
 * Represents any event sent by the server that is expecting some kind of response, regardless of whether or not that
 * response is required or in what format it is received.
 *
 * @see {@link PlayerInputInformEvent}
 * @see {@link PlayerInputRequestEvent}
 */
public interface PlayerInputEvent extends ServerQuestionEvent, DirectPlayerEvent
{
}
