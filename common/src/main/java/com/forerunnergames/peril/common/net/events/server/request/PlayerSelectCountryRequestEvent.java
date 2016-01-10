package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public class PlayerSelectCountryRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket player;
  private final ImmutableSet <CountryPacket> unownedCountries;

  public PlayerSelectCountryRequestEvent (final PlayerPacket player,
                                          final ImmutableSet <CountryPacket> unownedCountries)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (unownedCountries, "unownedCountries");

    this.player = player;
    this.unownedCountries = unownedCountries;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public ImmutableSet <CountryPacket> getUnownedCountries ()
  {
    return unownedCountries;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: {}", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryRequestEvent ()
  {
    player = null;
    unownedCountries = null;
  }
}
