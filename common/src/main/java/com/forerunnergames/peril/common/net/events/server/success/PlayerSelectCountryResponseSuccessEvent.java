package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class PlayerSelectCountryResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent, CountryOwnerChangedEvent
{
  private final CountryPacket selectedCountry;

  public PlayerSelectCountryResponseSuccessEvent (final PlayerPacket player, final CountryPacket selectedCountry)
  {
    super (player);

    this.selectedCountry = selectedCountry;
  }

  @Override
  public CountryPacket getCountry ()
  {
    return selectedCountry;
  }

  @Override
  public String getCountryName ()
  {
    return selectedCountry.getName ();
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

  public String getPlayerColor ()
  {
    return getPlayer ().getColor ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SelectedCountry: [{}]", super.toString (), selectedCountry);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseSuccessEvent ()
  {
    selectedCountry = null;
  }
}
