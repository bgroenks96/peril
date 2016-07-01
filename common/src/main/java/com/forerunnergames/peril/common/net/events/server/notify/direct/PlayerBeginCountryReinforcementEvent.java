package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.DirectPlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public class PlayerBeginCountryReinforcementEvent extends AbstractPlayerEvent implements DirectPlayerNotificationEvent
{
  private final ImmutableSet <CountryPacket> playerOwnedCountries;
  private final ImmutableSet <ContinentPacket> playerOwnedContinents;
  private final int maxArmiesPerCountry;

  public PlayerBeginCountryReinforcementEvent (final PlayerPacket player,
                                               final ImmutableSet <CountryPacket> playerOwnedCountries,
                                               final ImmutableSet <ContinentPacket> playerOwnedContinents,
                                               final int maxArmiesPerCountry)
  {
    super (player);

    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNull (playerOwnedContinents, "playerOwnedContinents");
    Arguments.checkIsNotNegative (maxArmiesPerCountry, "maxArmiesPerCountry");

    this.playerOwnedCountries = playerOwnedCountries;
    this.playerOwnedContinents = playerOwnedContinents;
    this.maxArmiesPerCountry = maxArmiesPerCountry;
  }

  public int getTotalReinforcements ()
  {
    return getPlayer ().getArmiesInHand ();
  }

  public int getMaxArmiesPerCountry ()
  {
    return maxArmiesPerCountry;
  }

  public ImmutableSet <CountryPacket> getPlayerOwnedCountries ()
  {
    return playerOwnedCountries;
  }

  public boolean isPlayerOwnedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    for (final CountryPacket country : playerOwnedCountries)
    {
      if (country.hasName (countryName)) return true;
    }

    return false;
  }

  public boolean isNotPlayerOwnedCountry (final String countryName)
  {
    return !isPlayerOwnedCountry (countryName);
  }

  public boolean canAddArmiesToCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    for (final CountryPacket country : playerOwnedCountries)
    {
      if (country.hasName (countryName)) return country.getArmyCount () < maxArmiesPerCountry;
    }

    return false;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | MaxArmiesPerCountry: {} | PlayerOwnedCountries: [{}] | PlayerOwnedContinents: [{}]",
                           super.toString (), maxArmiesPerCountry, playerOwnedCountries, playerOwnedContinents);
  }

  @RequiredForNetworkSerialization
  private PlayerBeginCountryReinforcementEvent ()
  {
    playerOwnedCountries = null;
    playerOwnedContinents = null;
    maxArmiesPerCountry = 0;
  }
}
