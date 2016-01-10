package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public final class CountryOwnerChangedEvent implements ServerNotificationEvent
{
  private final CountryPacket country;
  private final PlayerPacket previousOwner;
  private final PlayerPacket newOwner;

  public CountryOwnerChangedEvent (final CountryPacket country,
                                   final PlayerPacket previousOwner,
                                   final PlayerPacket newOwner)
  {
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkIsNotNull (previousOwner, "previousOwner");
    Arguments.checkIsNotNull (newOwner, "newOwner");

    this.country = country;
    this.previousOwner = previousOwner;
    this.newOwner = newOwner;
  }

  public CountryPacket getCountry ()
  {
    return country;
  }

  public PlayerPacket getPreviousOwner ()
  {
    return previousOwner;
  }

  public PlayerPacket getNewOwner ()
  {
    return newOwner;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Country: [{}] | PreviousOwner: [{}] | NewOwner: [{}]", getClass ().getSimpleName (),
                           country, previousOwner, newOwner);
  }

  @RequiredForNetworkSerialization
  private CountryOwnerChangedEvent ()
  {
    country = null;
    previousOwner = null;
    newOwner = null;
  }
}
