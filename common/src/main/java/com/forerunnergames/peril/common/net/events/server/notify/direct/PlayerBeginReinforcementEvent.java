package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.DirectPlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class PlayerBeginReinforcementEvent extends AbstractPlayerEvent implements DirectPlayerNotificationEvent
{
  private final ImmutableMap <String, Integer> playerOwnedCountryNamesToCountryArmyCounts;
  private final ImmutableSet <CountryPacket> playerOwnedCountries;
  private final ImmutableSet <ContinentPacket> playerOwnedContinents;
  private final int maxArmiesPerCountry;

  public PlayerBeginReinforcementEvent (final PlayerPacket player,
                                        final ImmutableSet <CountryPacket> playerOwnedCountries,
                                        final ImmutableSet <ContinentPacket> playerOwnedContinents,
                                        final int maxArmiesPerCountry)
  {
    super (player);

    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNull (playerOwnedContinents, "playerOwnedContinents");
    Arguments.checkIsNotNegative (maxArmiesPerCountry, "maxArmiesPerCountry");

    final ImmutableMap.Builder <String, Integer> builder = ImmutableMap.builder ();

    for (final CountryPacket country : playerOwnedCountries)
    {
      builder.put (country.getName (), country.getArmyCount ());
    }

    playerOwnedCountryNamesToCountryArmyCounts = builder.build ();

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

    return playerOwnedCountryNamesToCountryArmyCounts.containsKey (countryName);
  }

  public boolean isNotPlayerOwnedCountry (final String countryName)
  {
    return !isPlayerOwnedCountry (countryName);
  }

  public boolean canReinforceCountryWithSingleArmy (final String countryName)
  {
    return canReinforceCountryWithArmies (countryName, 1);
  }

  public boolean canReinforceCountryWithArmies (final String countryName, final int armies)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (armies, "armies");

    final Integer armyCount = playerOwnedCountryNamesToCountryArmyCounts.get (countryName);

    return armyCount != null && armyCount + armies <= maxArmiesPerCountry;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{} | MaxArmiesPerCountry: {} | PlayerOwnedCountries: [{}] | PlayerOwnedContinents: [{}] "
                                   + "| PlayerOwnedCountryNamesToCountryArmyCounts: [{}]",
                           super.toString (), maxArmiesPerCountry, playerOwnedCountryNamesToCountryArmyCounts,
                           playerOwnedContinents, playerOwnedCountryNamesToCountryArmyCounts);
  }

  @RequiredForNetworkSerialization
  private PlayerBeginReinforcementEvent ()
  {
    playerOwnedCountryNamesToCountryArmyCounts = null;
    playerOwnedCountries = null;
    playerOwnedContinents = null;
    maxArmiesPerCountry = 0;
  }
}
