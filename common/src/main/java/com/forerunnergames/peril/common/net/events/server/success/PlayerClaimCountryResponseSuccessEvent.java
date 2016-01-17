package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class PlayerClaimCountryResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent, CountryOwnerChangedEvent
{
  private final CountryPacket claimedCountry;

  public PlayerClaimCountryResponseSuccessEvent (final PlayerPacket player, final CountryPacket claimedCountry)
  {
    super (player);

    this.claimedCountry = claimedCountry;
  }

  @Override
  public CountryPacket getCountry ()
  {
    return claimedCountry;
  }

  @Override
  public String getCountryName ()
  {
    return claimedCountry.getName ();
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

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Claimed Country: [{}]", super.toString (), claimedCountry);
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryResponseSuccessEvent ()
  {
    claimedCountry = null;
  }
}
