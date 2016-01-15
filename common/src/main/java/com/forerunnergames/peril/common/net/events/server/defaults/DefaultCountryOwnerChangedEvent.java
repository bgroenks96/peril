package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class DefaultCountryOwnerChangedEvent extends AbstractCountryEvent implements CountryOwnerChangedEvent
{
  private final PlayerPacket newOwner;
  private final Optional <PlayerPacket> previousOwner;

  public DefaultCountryOwnerChangedEvent (final CountryPacket country,
                                          final PlayerPacket newOwner,
                                          final PlayerPacket previousOwner)
  {
    super (country);

    Arguments.checkIsNotNull (newOwner, "newOwner");
    Arguments.checkIsNotNull (previousOwner, "previousOwner");

    this.newOwner = newOwner;
    this.previousOwner = Optional.of (previousOwner);
  }

  public DefaultCountryOwnerChangedEvent (final CountryPacket country, final PlayerPacket newOwner)
  {
    super (country);

    Arguments.checkIsNotNull (newOwner, "newOwner");

    this.newOwner = newOwner;

    previousOwner = Optional.absent ();
  }

  @Override
  public Optional <PlayerPacket> getPreviousOwner ()
  {
    return previousOwner;
  }

  @Override
  public PlayerPacket getNewOwner ()
  {
    return newOwner;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | NewOwner: [{}] | PreviousOwner: [{}]", super.toString (), newOwner, previousOwner);
  }

  @RequiredForNetworkSerialization
  private DefaultCountryOwnerChangedEvent ()
  {
    newOwner = null;
    previousOwner = null;
  }
}
