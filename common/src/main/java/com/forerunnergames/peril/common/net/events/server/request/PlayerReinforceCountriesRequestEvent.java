package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerReinforceCountriesRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket player;
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
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNegative (countryReinforcementBonus, "countryReinforcementBonus");
    Arguments.checkIsNotNegative (continentReinforcementBonus, "contienentReinforcementBonus");
    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNull (playerOwnedContinents, "playerOwnedContinents");

    this.player = player;
    this.countryReinforcementBonus = countryReinforcementBonus;
    this.continentReinforcementBonus = continentReinforcementBonus;
    this.playerOwnedCountries = playerOwnedCountries;
    this.playerOwnedContinents = playerOwnedContinents;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
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

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Player: [{}] | CountryReinforcementBonus: {} | ContinentReinforcementBonus: {} | PlayerOwnedCountries: [{}] | PlayerOwnedContinents: [{}]",
                           getClass ().getSimpleName (), player, countryReinforcementBonus, continentReinforcementBonus,
                           playerOwnedCountries, playerOwnedContinents);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesRequestEvent ()
  {
    player = null;
    countryReinforcementBonus = 0;
    continentReinforcementBonus = 0;
    playerOwnedCountries = null;
    playerOwnedContinents = null;
  }
}
