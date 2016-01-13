package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMultimap;

public final class PlayerFortifyCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final ImmutableMultimap <CountryPacket, CountryPacket> fortifyVectors;

  public PlayerFortifyCountryRequestEvent (final PlayerPacket player,
                                           final ImmutableMultimap <CountryPacket, CountryPacket> fortifyVectors)
  {
    super (player);

    Arguments.checkIsNotNull (fortifyVectors, "fortifyVectors");

    this.fortifyVectors = fortifyVectors;
  }

  public ImmutableMultimap <CountryPacket, CountryPacket> getValidFortifyVectors ()
  {
    return fortifyVectors;
  }

  @Override
  public String toString ()
  {
    return Strings.format (" | FortifyVectors: [{}]", super.toString (), fortifyVectors);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryRequestEvent ()
  {
    fortifyVectors = null;
  }
}
