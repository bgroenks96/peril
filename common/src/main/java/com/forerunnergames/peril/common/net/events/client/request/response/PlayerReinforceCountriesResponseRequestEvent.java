package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.collect.ImmutableMap;

public class PlayerReinforceCountriesResponseRequestEvent implements ResponseRequestEvent
{
  private final CardSetPacket tradeIn;
  private final ImmutableMap <String, Integer> countryNamesToReinforcements;

  public PlayerReinforceCountriesResponseRequestEvent (final ImmutableMap <String, Integer> countryNamesToReinforcements,
                                                       final CardSetPacket tradeIn)
  {
    Arguments.checkIsNotNull (tradeIn, "tradeIn");
    Arguments.checkIsNotNull (countryNamesToReinforcements, "countryNamesToReinforcements");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToReinforcements, "countryNamesToReinforcements");

    this.tradeIn = tradeIn;
    this.countryNamesToReinforcements = countryNamesToReinforcements;
  }

  public CardSetPacket getTradeIn ()
  {
    return tradeIn;
  }

  public ImmutableMap <String, Integer> getReinforcedCountries ()
  {
    return countryNamesToReinforcements;
  }
  
  @Override
  public String toString ()
  {
    return Strings.format ("{}: Trade-In: [{}] | Reinforcements: [{}]", this.getClass ().getSimpleName (), tradeIn, countryNamesToReinforcements);
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerReinforceCountriesRequestEvent.class;
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesResponseRequestEvent ()
  {
    tradeIn = null;
    countryNamesToReinforcements = null;
  }
}
