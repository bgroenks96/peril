package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.defaults.AbstractInternalCommunicationEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public class NotifyPlayerInputTimeoutEvent extends AbstractInternalCommunicationEvent
{
  private final PlayerInputEvent timedOutEvent;

  public NotifyPlayerInputTimeoutEvent (final PlayerInputEvent timedOutEvent)
  {
    Arguments.checkIsNotNull (timedOutEvent, "timedOutEvent");

    this.timedOutEvent = timedOutEvent;
  }

  public PlayerInputEvent getTimedOutEvent ()
  {
    return timedOutEvent;
  }

  public PlayerPacket getPlayer ()
  {
    return timedOutEvent.getPerson ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | TimedOutEvent: [{}]", super.toString (), timedOutEvent);
  }
}
