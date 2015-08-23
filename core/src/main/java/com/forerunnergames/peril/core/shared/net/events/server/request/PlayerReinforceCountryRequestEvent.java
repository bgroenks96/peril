package com.forerunnergames.peril.core.shared.net.events.server.request;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public class PlayerReinforceCountryRequestEvent extends PlayerSelectCountryRequestEvent
{
  private final ImmutableSet <CountryPacket> playerCountries;

  public PlayerReinforceCountryRequestEvent (final PlayerPacket player,
                                             final ImmutableSet <CountryPacket> playerCountries)
  {
    super (player);

    this.playerCountries = playerCountries;
  }

  public ImmutableSet <CountryPacket> getOwnedCountries ()
  {
    return playerCountries;
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountryRequestEvent ()
  {
    super (null);
    playerCountries = null;
  }
}
