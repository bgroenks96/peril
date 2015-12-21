package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableMultimap;

public final class BeginAttackPhaseEvent implements ServerNotificationEvent
{
  private final PlayerPacket currentPlayer;
  private final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors;

  public BeginAttackPhaseEvent (final PlayerPacket currentPlayer,
                                final ImmutableMultimap <CountryPacket, CountryPacket> validAttackVectors)
  {
    Arguments.checkIsNotNull (currentPlayer, "currentPlayer");
    Arguments.checkIsNotNull (validAttackVectors, "validAttackVectors");

    this.currentPlayer = currentPlayer;
    this.validAttackVectors = validAttackVectors;
  }

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
    return Strings.format ("{}: Player: [{}]", getClass ().getSimpleName (), currentPlayer);
  }

  @RequiredForNetworkSerialization
  public BeginAttackPhaseEvent ()
  {
    currentPlayer = null;
    validAttackVectors = null;
  }
}
