package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

import java.util.concurrent.TimeUnit;

public final class PlayerResponseTimeoutEvent extends AbstractPlayerEvent implements BroadcastNotificationEvent
{
  private final String eventTypeName;
  private final long timeout;
  private final TimeUnit timeUnit;

  public PlayerResponseTimeoutEvent (final PlayerPacket player,
                                     final String eventTypeName,
                                     final long timeout,
                                     final TimeUnit timeUnit)
  {
    super (player);

    Arguments.checkIsNotNull (eventTypeName, "eventTypeName");
    Arguments.checkIsNotNegative (timeout, "timeout");
    Arguments.checkIsNotNull (timeUnit, "timeUnit");

    this.eventTypeName = eventTypeName;
    this.timeout = timeout;
    this.timeUnit = timeUnit;
  }

  public long getTimeoutAsMillis ()
  {
    return timeUnit.toMillis (timeout);
  }

  public long getTimeout ()
  {
    return timeout;
  }

  public TimeUnit getTimeUnit ()
  {
    return timeUnit;
  }

  public String getEventTypeName ()
  {
    return eventTypeName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Timeout: {} | TimeUnit: {}", super.toString (), timeout, timeUnit);
  }

  @RequiredForNetworkSerialization
  private PlayerResponseTimeoutEvent ()
  {
    eventTypeName = null;
    timeout = 0;
    timeUnit = null;
  }
}
