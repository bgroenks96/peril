package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

/*
 * Copyright © 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import com.forerunnergames.peril.common.net.events.client.request.response.PlayerReinforceCountriesResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerReinforceInitialCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceInitialCountryRequestEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO This class is a hack until the core reinforcement event API can be fixed.
public final class ReinforcementRequestHelper
{
  private static final Logger log = LoggerFactory.getLogger (ReinforcementRequestHelper.class);
  @Nullable
  private PlayerReinforceCountriesRequestEvent regularRequest;
  @Nullable
  private PlayerReinforceInitialCountryRequestEvent initialRequest;

  public boolean isSet ()
  {
    return regularRequest != null || initialRequest != null;
  }

  void set (final Object request)
  {
    Arguments.checkIsNotNull (request, "request");

    if (request instanceof PlayerReinforceCountriesRequestEvent)
    {
      regularRequest = (PlayerReinforceCountriesRequestEvent) request;
      initialRequest = null;
      return;
    }

    if (request instanceof PlayerReinforceInitialCountryRequestEvent)
    {
      initialRequest = (PlayerReinforceInitialCountryRequestEvent) request;
      regularRequest = null;
      return;
    }

    log.warn ("Not setting unrecognized request object: {}.", request);
  }

  ImmutableSet <CountryPacket> getPlayerOwnedCountries ()
  {
    if (regularRequest != null) return regularRequest.getPlayerOwnedCountries ();
    if (initialRequest != null) return initialRequest.getPlayerOwnedCountries ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return ImmutableSet.of ();
  }

  boolean isPlayerOwnedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (regularRequest != null) return regularRequest.isPlayerOwnedCountry (countryName);
    if (initialRequest != null) return initialRequest.isPlayerOwnedCountry (countryName);

    log.warn ("No prior corresponding reinforcement request was received.");

    return false;
  }

  boolean isNotPlayerOwnedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (regularRequest != null) return regularRequest.isNotPlayerOwnedCountry (countryName);
    if (initialRequest != null) return initialRequest.isNotPlayerOwnedCountry (countryName);

    log.warn ("No prior corresponding reinforcement request was received.");

    return true;
  }

  boolean canAddArmiesToCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (regularRequest != null) return regularRequest.canAddArmiesToCountry (countryName);
    if (initialRequest != null) return initialRequest.canAddArmiesToCountry (countryName);

    log.warn ("No prior corresponding reinforcement request was received.");

    return false;
  }

  int getMaxArmiesPerCountry ()
  {
    if (regularRequest != null) return regularRequest.getMaxArmiesPerCountry ();
    if (initialRequest != null) return initialRequest.getMaxArmiesPerCountry ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return 0;
  }

  int getTotalReinforcements ()
  {
    if (regularRequest != null) return regularRequest.getTotalReinforcements ();
    if (initialRequest != null) return initialRequest.getPreSetReinforcementCount ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return 0;
  }

  String getPlayerName ()
  {
    if (regularRequest != null) return regularRequest.getPlayerName ();
    if (initialRequest != null) return initialRequest.getPlayerName ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return "";
  }

  String getClassName ()
  {
    if (regularRequest != null) return regularRequest.getClass ().getSimpleName ();
    if (initialRequest != null) return initialRequest.getClass ().getSimpleName ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return "";
  }

  void sendResponse (final ImmutableMap <String, Integer> countryNamesToReinforcements,
                     final MBassador <Event> eventBus)
  {
    if (regularRequest != null)
    {
      eventBus.publish (new PlayerReinforceCountriesResponseRequestEvent (countryNamesToReinforcements));
      return;
    }

    if (initialRequest != null)
    {
      eventBus.publish (new PlayerReinforceInitialCountryResponseRequestEvent (
              countryNamesToReinforcements.keySet ().iterator ().next ()));
      return;
    }

    log.warn ("No prior corresponding reinforcement request was received.");
  }

  void reset ()
  {
    regularRequest = null;
    initialRequest = null;
  }
}
