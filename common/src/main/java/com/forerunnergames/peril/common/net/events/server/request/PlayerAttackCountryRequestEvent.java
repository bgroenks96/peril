package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMultimap;

public final class PlayerAttackCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors;

  public PlayerAttackCountryRequestEvent (final PlayerPacket currentPlayer,
                                          final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors)
  {
    super (currentPlayer);

    Arguments.checkIsNotNull (validAttackVectors, "validAttackVectors");

    this.validAttackVectors = validAttackVectors;
  }

  public ImmutableMultimap <CountryPacket, CountryPacket> getValidAttackVectors ()
  {
    return validAttackVectors;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | AttackVectors: [{}]", super.toString (), validAttackVectors);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryRequestEvent ()
  {
    validAttackVectors = null;
  }
}
