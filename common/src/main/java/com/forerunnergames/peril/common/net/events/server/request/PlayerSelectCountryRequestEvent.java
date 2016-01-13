package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerSelectCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final ImmutableSet <CountryPacket> unownedCountries;

  public PlayerSelectCountryRequestEvent (final PlayerPacket player,
                                          final ImmutableSet <CountryPacket> unownedCountries)
  {
    super (player);

    Arguments.checkIsNotNull (unownedCountries, "unownedCountries");

    this.unownedCountries = unownedCountries;
  }

  public ImmutableSet <CountryPacket> getUnownedCountries ()
  {
    return unownedCountries;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | UnownedCountries: [{}]", super.toString (), unownedCountries);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryRequestEvent ()
  {
    unownedCountries = null;
  }
}
