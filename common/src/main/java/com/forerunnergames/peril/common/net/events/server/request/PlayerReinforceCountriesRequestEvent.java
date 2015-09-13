package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerReinforceCountriesRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket player;
  private final int countryReinforcementBonus;
  private final int continentReinforcementBonus;
  private final int nextCardTradeInBonus;
  private final ImmutableSet <CountryPacket> playerOwnedCountries;
  private final ImmutableSet <CardSetPacket> tradeInMatches;
  private final boolean tradeInRequired;

  public PlayerReinforceCountriesRequestEvent (final PlayerPacket player,
                                               final int countryReinforcementBonus,
                                               final int continentReinforcementBonus,
                                               final int nextCardTradeInBonus,
                                               final ImmutableSet <CountryPacket> playerOwnedCountries,
                                               final ImmutableSet <CardSetPacket> tradeInMatches,
                                               final boolean tradeInRequired)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNegative (countryReinforcementBonus, "countryReinforcementBonus");
    Arguments.checkIsNotNegative (continentReinforcementBonus, "contienentReinforcementBonus");
    Arguments.checkIsNotNegative (nextCardTradeInBonus, "nextCardTradeInBonus");
    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkHasNoNullElements (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNull (tradeInMatches, "tradeInMatches");
    Arguments.checkHasNoNullElements (tradeInMatches, "tradeInMatches");

    this.player = player;
    this.countryReinforcementBonus = countryReinforcementBonus;
    this.continentReinforcementBonus = continentReinforcementBonus;
    this.nextCardTradeInBonus = nextCardTradeInBonus;
    this.playerOwnedCountries = playerOwnedCountries;
    this.tradeInMatches = tradeInMatches;
    this.tradeInRequired = tradeInRequired;
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
  
  public int getNextCardTradeInBonus ()
  {
    return nextCardTradeInBonus;
  }
  
  public ImmutableSet <CountryPacket> getPlayerOwnedCountries ()
  {
    return playerOwnedCountries;
  }

  public ImmutableSet <CardSetPacket> getMatches ()
  {
    return tradeInMatches;
  }

  public boolean isTradeInRequired ()
  {
    return tradeInRequired;
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesRequestEvent ()
  {
    player = null;
    countryReinforcementBonus = 0;
    continentReinforcementBonus = 0;
    nextCardTradeInBonus = 0;
    playerOwnedCountries = null;
    tradeInMatches = null;
    tradeInRequired = false;
  }
}
