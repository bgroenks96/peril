package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerReinforceCountriesRequestEvent extends AbstractPlayerArmiesChangedEvent
        implements PlayerInputRequestEvent, PlayerArmiesChangedEvent
{
  private final int countryReinforcementBonus;
  private final int continentReinforcementBonus;
  private final ImmutableSet <CountryPacket> playerOwnedCountries;
  private final ImmutableSet <ContinentPacket> playerOwnedContinents;

  public PlayerReinforceCountriesRequestEvent (final PlayerPacket player,
                                               final int countryReinforcementBonus,
                                               final int continentReinforcementBonus,
                                               final ImmutableSet <CountryPacket> playerOwnedCountries,
                                               final ImmutableSet <ContinentPacket> playerOwnedContinents)
  {
    super (player, countryReinforcementBonus + continentReinforcementBonus); // total armies changed

    Arguments.checkIsNotNegative (countryReinforcementBonus, "countryReinforcementBonus");
    Arguments.checkIsNotNegative (continentReinforcementBonus, "contienentReinforcementBonus");
    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNull (playerOwnedContinents, "playerOwnedContinents");

    this.countryReinforcementBonus = countryReinforcementBonus;
    this.continentReinforcementBonus = continentReinforcementBonus;
    this.playerOwnedCountries = playerOwnedCountries;
    this.playerOwnedContinents = playerOwnedContinents;
  }

  public int getCountryReinforcementBonus ()
  {
    return countryReinforcementBonus;
  }

  public int getContinentReinforcementBonus ()
  {
    return continentReinforcementBonus;
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

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{} | CountryReinforcementBonus: {} | ContinentReinforcementBonus: {} | PlayerOwnedCountries: [{}] | PlayerOwnedContinents: [{}]",
                           super.toString (), countryReinforcementBonus, continentReinforcementBonus,
                           playerOwnedCountries, playerOwnedContinents);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesRequestEvent ()
  {
    countryReinforcementBonus = 0;
    continentReinforcementBonus = 0;
    playerOwnedCountries = null;
    playerOwnedContinents = null;
  }
}
