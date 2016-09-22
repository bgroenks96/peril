package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

public class DefaultInboundPlayerResponseRequestEvent <T extends ResponseRequestEvent, R extends PlayerInputRequestEvent>
        extends AbstractInternalCommunicationEvent implements InboundPlayerResponseRequestEvent <T, R>
{
  private final PlayerPacket player;
  private final T responseRequestEvent;
  private final R inputRequestEvent;

  public DefaultInboundPlayerResponseRequestEvent (final PlayerPacket player,
                                                   final T responseRequestEvent,
                                                   final R inputRequestEvent)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (responseRequestEvent, "responseRequestEvent");
    Arguments.checkIsNotNull (inputRequestEvent, "inputRequestEvent");

    this.player = player;
    this.responseRequestEvent = responseRequestEvent;
    this.inputRequestEvent = inputRequestEvent;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public T getRequestEvent ()
  {
    return responseRequestEvent;
  }

  @Override
  public R getOriginalRequestEvent ()
  {
    return inputRequestEvent;
  }

}
