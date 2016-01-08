package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMultimap;

public class PlayerFortifyCountryRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket player;
  private final ImmutableMultimap <CountryPacket, CountryPacket> fortifyVectors;

  public PlayerFortifyCountryRequestEvent (final PlayerPacket player,
                                           final ImmutableMultimap <CountryPacket, CountryPacket> fortifyVectors)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (fortifyVectors, "fortifyVectors");

    this.player = player;
    this.fortifyVectors = fortifyVectors;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public ImmutableMultimap <CountryPacket, CountryPacket> getValidFortifyVectors ()
  {
    return fortifyVectors;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | FortifyVectors: [{}]", getClass ().getSimpleName (), player,
                           fortifyVectors);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryRequestEvent ()
  {
    player = null;
    fortifyVectors = null;
  }
}
