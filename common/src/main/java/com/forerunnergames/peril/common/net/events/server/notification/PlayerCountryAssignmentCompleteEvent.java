package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
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
  private final InitialCountryAssignment assignmentMode;
  private final ImmutableMap <CountryPacket, PlayerPacket> countryToPlayerPackets;

  public PlayerCountryAssignmentCompleteEvent (final InitialCountryAssignment assignmentMode,
                                               final ImmutableMap <CountryPacket, PlayerPacket> countryToPlayerPackets)
  {
    Arguments.checkIsNotNull (assignmentMode, "assignmentMode");
    Arguments.checkIsNotNull (countryToPlayerPackets, "countryToPlayerPackets");
    Arguments.checkHasNoNullKeysOrValues (countryToPlayerPackets, "countryToPlayerPackets");

    this.assignmentMode = assignmentMode;
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

  public InitialCountryAssignment getAssignmentMode ()
  {
    return assignmentMode;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: AssignmentMode: {} | CountryToPlayerPackets: [{}]", getClass ().getSimpleName (),
                           assignmentMode, Strings.toString (countryToPlayerPackets));
  }

  @RequiredForNetworkSerialization
  private PlayerCountryAssignmentCompleteEvent ()
  {
    assignmentMode = null;
    countryToPlayerPackets = null;
  }
}
