package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.GameNotificationEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class PlayerCountryAssignmentCompleteEvent implements GameNotificationEvent
{
  private final ImmutableMap <CountryPacket, PlayerPacket> countryToPlayerPackets;

  public PlayerCountryAssignmentCompleteEvent (final ImmutableMap <CountryPacket, PlayerPacket> countryToPlayerPackets)
  {
    Arguments.checkIsNotNull (countryToPlayerPackets, "countryToPlayerPackets");
    Arguments.checkHasNoNullKeysOrValues (countryToPlayerPackets, "countryToPlayerPackets");

    this.countryToPlayerPackets = countryToPlayerPackets;
  }

  public ImmutableSet <CountryPacket> getCountries ()
  {
    return countryToPlayerPackets.keySet ();
  }

  public ImmutableSet <PlayerPacket> getPlayers ()
  {
    return ImmutableSet.copyOf (countryToPlayerPackets.values ());
  }

  public PlayerPacket getOwner (final CountryPacket country)
  {
    return countryToPlayerPackets.get (country);
  }

  @RequiredForNetworkSerialization
  private PlayerCountryAssignmentCompleteEvent ()
  {
    countryToPlayerPackets = null;
  }
}
