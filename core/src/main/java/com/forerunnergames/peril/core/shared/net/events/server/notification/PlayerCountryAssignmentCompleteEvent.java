package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.GameNotificationEvent;
import com.forerunnergames.peril.core.shared.net.packets.CountryPacket;
import com.forerunnergames.peril.core.shared.net.packets.GamePackets;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class PlayerCountryAssignmentCompleteEvent implements GameNotificationEvent
{
  private final ImmutableMap <CountryPacket, PlayerPacket> countryToPlayerPackets;

  public PlayerCountryAssignmentCompleteEvent (final ImmutableMap <Country, Player> countriesToPlayers)
  {
    Arguments.checkIsNotNull (countriesToPlayers, "countriesToPlayers");

    this.countryToPlayerPackets = GamePackets.fromPlayMap (countriesToPlayers);
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
