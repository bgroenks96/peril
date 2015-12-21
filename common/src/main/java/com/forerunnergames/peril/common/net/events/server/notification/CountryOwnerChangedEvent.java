package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public final class CountryOwnerChangedEvent implements ServerNotificationEvent
{
  private final CountryPacket country;
  private final PlayerPacket previousOwner;
  private final PlayerPacket currentOwner;

  public CountryOwnerChangedEvent (final CountryPacket country,
                                   final PlayerPacket previousOwner,
                                   final PlayerPacket currentOwner)
  {
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkIsNotNull (previousOwner, "previousOwner");
    Arguments.checkIsNotNull (currentOwner, "currentOwner");

    this.country = country;
    this.previousOwner = previousOwner;
    this.currentOwner = currentOwner;
  }

  public CountryPacket getCountry ()
  {
    return country;
  }

  public PlayerPacket getPreviousOwner ()
  {
    return previousOwner;
  }

  public PlayerPacket getCurrentOwner ()
  {
    return currentOwner;
  }
}
