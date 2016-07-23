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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.events.SelectCountryEvent;
import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Enveloped;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.subscription.MessageEnvelope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReinforcementPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (ReinforcementPhaseHandler.class);
  private static final int REINFORCEMENTS_PER_COUNTRY_SELECTION = 1;
  private final Map <String, Integer> countryNamesToReinforcementsResponseData = new HashMap<> ();
  private final MBassador <Event> eventBus;
  private final ReinforcementRequestHelper requestHelper = new ReinforcementRequestHelper ();
  private PlayMap playMap;
  private int totalReinforcementsPlaced = 0;

  public ReinforcementPhaseHandler (final PlayMap playMap, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.eventBus = eventBus;
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  @Handler
  @Enveloped (messages = { PlayerCardTradeInAvailableEvent.class })
  void onEvent (final MessageEnvelope envelope)
  {
    Arguments.checkIsNotNull (envelope, "envelope");

    log.trace ("Event received [{}].", envelope.getMessage ());

    requestHelper.set (envelope.getMessage ());

    askPlayerToChooseACountry (requestHelper.getPlayerName ());
  }

  @Handler
  void onEvent (final SelectCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    final String countryName = event.getCountryName ();

    if (checkRequestExistsFor (event).failed ()) return;
    if (checkContainsPlayerOwnedCountry (event).failed ()) return;
    if (checkCanReinforceCountry (countryName).failed ()) return;

    reinforceCountry (countryName);

    if (reinforcementIsNotFinished ())
    {
      log.info ("{} of {} reinforcements remaining to be placed.",
                requestHelper.getTotalReinforcements () - totalReinforcementsPlaced,
                requestHelper.getTotalReinforcements ());
      return;
    }

    sendResponse ();
    reset ();
  }

  private void askPlayerToChooseACountry (final String playerName)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        eventBus.publish (StatusMessageEventFactory
                .create (Strings.format ("{}, choose a country to reinforce.", playerName)));
      }
    });
  }

  private Result <String> checkRequestExistsFor (final SelectCountryEvent event)
  {
    if (!requestHelper.isSet ())
    {
      final String failureMessage = Strings
              .format ("Ignoring [{}] because no prior corresponding reinforcement request was received.", event);
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkContainsPlayerOwnedCountry (final SelectCountryEvent event)
  {
    if (isNotPlayerOwnedCountry (event.getCountryName ()))
    {
      final String failureMessage = Strings.format ("Ignoring local event [{}] because not a valid response to [{}].",
                                                    event, requestHelper.getClassName ());
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkCanReinforceCountry (final String countryName)
  {
    if (!canReinforceCountry (countryName))
    {
      final String failureMessage = Strings.format (
                                                    "Cannot reinforce country [{}] because it already contains the "
                                                            + "maximum number of armies: [{}].",
                                                    countryName, requestHelper.getMaxArmiesPerCountry ());
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private boolean isNotPlayerOwnedCountry (final String countryName)
  {
    return requestHelper.isNotPlayerOwnedCountry (countryName);
  }

  private boolean canReinforceCountry (final String countryName)
  {
    return requestHelper.getMaxArmiesPerCountry ()
            - getReinforcementsPlacedOn (countryName) >= REINFORCEMENTS_PER_COUNTRY_SELECTION;
  }

  private int getReinforcementsPlacedOn (final String countryName)
  {
    final Integer countryArmyCount = countryNamesToReinforcementsResponseData.get (countryName);

    if (countryArmyCount == null)
    {
      countryNamesToReinforcementsResponseData.put (countryName, 0);
      return 0;
    }

    return countryArmyCount;
  }

  private void reinforceCountry (final String countryName)
  {
    saveReinforcementOf (countryName);
    reinforceCountryInPlayMap (countryName);
    updateTotalReinforcementsPlaced ();

    log.info ("Reinforced {} with {}.", countryName,
              Strings.pluralize (REINFORCEMENTS_PER_COUNTRY_SELECTION, "army", "armies"));
  }

  private void saveReinforcementOf (final String countryName)
  {
    final Integer countryArmyCount = countryNamesToReinforcementsResponseData.get (countryName);

    if (countryArmyCount == null)
    {
      countryNamesToReinforcementsResponseData.put (countryName, REINFORCEMENTS_PER_COUNTRY_SELECTION);
      return;
    }

    countryNamesToReinforcementsResponseData.put (countryName, countryArmyCount + REINFORCEMENTS_PER_COUNTRY_SELECTION);
  }

  private void reinforceCountryInPlayMap (final String countryName)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMap.changeArmiesBy (REINFORCEMENTS_PER_COUNTRY_SELECTION, countryName);
      }
    });
  }

  private void updateTotalReinforcementsPlaced ()
  {
    totalReinforcementsPlaced += REINFORCEMENTS_PER_COUNTRY_SELECTION;
  }

  private boolean reinforcementIsNotFinished ()
  {
    return totalReinforcementsPlaced < requestHelper.getTotalReinforcements ();
  }

  private void sendResponse ()
  {
    requestHelper.sendResponse (ImmutableMap.copyOf (countryNamesToReinforcementsResponseData), eventBus);
  }

  private void reset ()
  {
    requestHelper.reset ();
    totalReinforcementsPlaced = 0;
    countryNamesToReinforcementsResponseData.clear ();
  }
}
