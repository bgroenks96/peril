package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class PlayerClaimCountryResponseSuccessEvent extends AbstractPlayerCountryArmiesChangedEvent
        implements PlayerResponseSuccessEvent, CountryOwnerChangedEvent
{
  public PlayerClaimCountryResponseSuccessEvent (final PlayerPacket player,
                                                 final CountryPacket claimedCountry,
                                                 final int deltaArmyCount)
  {
    super (player, claimedCountry, -deltaArmyCount, deltaArmyCount);
  }

  @Override
  public Optional <PlayerPacket> getPreviousOwner ()
  {
    return Optional.absent ();
  }

  @Override
  public PlayerPacket getNewOwner ()
  {
    return getPlayer ();
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryResponseSuccessEvent ()
  {
  }
}
