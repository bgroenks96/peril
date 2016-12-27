package com.forerunnergames.peril.common.net.events.server.interfaces;

/**
 * Represents any event sent by the server that is expecting some kind of response, regardless of whether or not that
 * response is required or in what format it is received.
 *
 * @see {@link PlayerInformEvent}
 * @see {@link PlayerInputRequestEvent}
 */
public interface PlayerInputEvent extends DirectPlayerEvent
{
}
