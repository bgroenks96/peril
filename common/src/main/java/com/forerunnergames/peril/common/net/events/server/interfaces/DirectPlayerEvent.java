package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.tools.net.events.remote.DirectEvent;

/**
 * Combination type for PlayerEvent and DirectEvent.
 */
public interface DirectPlayerEvent extends DirectEvent, PlayerEvent
{
}
