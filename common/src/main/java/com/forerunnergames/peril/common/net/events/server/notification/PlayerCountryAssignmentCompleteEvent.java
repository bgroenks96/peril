package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class PlayerCountryAssignmentCompleteEvent implements ServerNotificationEvent
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
    Arguments.checkIsNotNull (country, "country");

    return countryToPlayerPackets.get (country);
  }

  public String getOwnerColor (final CountryPacket country)
  {
    Arguments.checkIsNotNull (country, "country");

    return countryToPlayerPackets.get (country).getColor ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: CountryToPlayerPackets: [{}]", getClass ().getSimpleName (),
                           Strings.toString (countryToPlayerPackets));
  }

  @RequiredForNetworkSerialization
  private PlayerCountryAssignmentCompleteEvent ()
  {
    countryToPlayerPackets = null;
  }
}
