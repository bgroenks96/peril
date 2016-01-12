package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.collect.ImmutableMap;

public final class PlayerReinforceCountriesResponseRequestEvent implements ResponseRequestEvent
{
  private final ImmutableMap <String, Integer> countryNamesToReinforcements;

  public PlayerReinforceCountriesResponseRequestEvent (final ImmutableMap <String, Integer> countryNamesToReinforcements)
  {
    Arguments.checkIsNotNull (countryNamesToReinforcements, "countryNamesToReinforcements");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToReinforcements, "countryNamesToReinforcements");

    this.countryNamesToReinforcements = countryNamesToReinforcements;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerReinforceCountriesRequestEvent.class;
  }

  public ImmutableMap <String, Integer> getReinforcedCountries ()
  {
    return countryNamesToReinforcements;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Reinforcements: [{}]", getClass ().getSimpleName (), countryNamesToReinforcements);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesResponseRequestEvent ()
  {
    countryNamesToReinforcements = null;
  }
}
