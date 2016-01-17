package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerClaimCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final ImmutableSet <CountryPacket> unclaimedCountries;

  public PlayerClaimCountryRequestEvent (final PlayerPacket player,
                                         final ImmutableSet <CountryPacket> unclaimedCountries)
  {
    super (player);

    Arguments.checkIsNotNull (unclaimedCountries, "unclaimedCountries");

    this.unclaimedCountries = unclaimedCountries;
  }

  public ImmutableSet <CountryPacket> getUnclaimedCountries ()
  {
    return unclaimedCountries;
  }

  public boolean isUnclaimedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    for (final CountryPacket country : unclaimedCountries)
    {
      if (country.hasName (countryName)) return true;
    }

    return false;
  }

  public boolean isClaimedCountry (final String countryName)
  {
    return !isUnclaimedCountry (countryName);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Unclaimed Countries: [{}]", super.toString (), unclaimedCountries);
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryRequestEvent ()
  {
    unclaimedCountries = null;
  }
}
