package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerReinforceCountriesRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final ImmutableSet <CountryPacket> playerOwnedCountries;
  private final ImmutableSet <ContinentPacket> playerOwnedContinents;
  private final int countryReinforcementBonus;
  private final int continentReinforcementBonus;
  private final int maxArmiesPerCountry;

  public PlayerReinforceCountriesRequestEvent (final PlayerPacket player,
                                               final ImmutableSet <CountryPacket> playerOwnedCountries,
                                               final ImmutableSet <ContinentPacket> playerOwnedContinents,
                                               final int countryReinforcementBonus,
                                               final int continentReinforcementBonus,
                                               final int maxArmiesPerCountry)
  {
    super (player);

    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNull (playerOwnedContinents, "playerOwnedContinents");
    Arguments.checkIsNotNegative (countryReinforcementBonus, "countryReinforcementBonus");
    Arguments.checkIsNotNegative (continentReinforcementBonus, "continentReinforcementBonus");
    Arguments.checkIsNotNegative (maxArmiesPerCountry, "maxArmiesPerCountry");

    this.playerOwnedCountries = playerOwnedCountries;
    this.playerOwnedContinents = playerOwnedContinents;
    this.countryReinforcementBonus = countryReinforcementBonus;
    this.continentReinforcementBonus = continentReinforcementBonus;
    this.maxArmiesPerCountry = maxArmiesPerCountry;
  }

  public int getCountryReinforcementBonus ()
  {
    return countryReinforcementBonus;
  }

  public int getContinentReinforcementBonus ()
  {
    return continentReinforcementBonus;
  }

  public int getTotalReinforcements ()
  {
    return getPlayer ().getArmiesInHand () + countryReinforcementBonus + continentReinforcementBonus;
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
    return Strings.format (
                           "{} | CountryReinforcementBonus: {} | ContinentReinforcementBonus: {} | MaxArmiesPerCountry: {} | PlayerOwnedCountries: [{}] | PlayerOwnedContinents: [{}]",
                           super.toString (), countryReinforcementBonus, continentReinforcementBonus,
                           maxArmiesPerCountry, playerOwnedCountries, playerOwnedContinents);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesRequestEvent ()
  {
    playerOwnedCountries = null;
    playerOwnedContinents = null;
    countryReinforcementBonus = 0;
    continentReinforcementBonus = 0;
    maxArmiesPerCountry = 0;
  }
}
