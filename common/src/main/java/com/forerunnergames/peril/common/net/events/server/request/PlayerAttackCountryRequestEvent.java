package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMultimap;

public final class PlayerAttackCountryRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket currentPlayer;
  private final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors;

  public PlayerAttackCountryRequestEvent (final PlayerPacket currentPlayer,
                                          final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors)
  {
    Arguments.checkIsNotNull (currentPlayer, "currentPlayer");
    Arguments.checkIsNotNull (validAttackVectors, "validAttackVectors");

    this.currentPlayer = currentPlayer;
    this.validAttackVectors = validAttackVectors;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return currentPlayer;
  }

  public ImmutableMultimap <CountryPacket, CountryPacket> getValidAttackVectors ()
  {
    return validAttackVectors;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | AttackVectors: [{}]", getClass ().getSimpleName (), currentPlayer,
                           validAttackVectors);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryRequestEvent ()
  {
    currentPlayer = null;
    validAttackVectors = null;
  }
}
