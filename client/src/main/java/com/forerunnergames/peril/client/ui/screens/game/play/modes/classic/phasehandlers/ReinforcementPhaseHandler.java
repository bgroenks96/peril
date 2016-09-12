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
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.status.StatusMessageEventGenerator;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReinforcementPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (ReinforcementPhaseHandler.class);
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  @Nullable
  private PlayerBeginReinforcementEvent beginReinforcementEvent;

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
  void onEvent (final PlayerBeginReinforcementEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    beginReinforcementEvent = event;

    askPlayerToChooseACountry (event.getPlayerName ());
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

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        // Preemptively update the play map for responsive UI (assume success from server).
        playMap.changeArmiesBy (1, countryName);
      }
    });

    eventBus.publish (new PlayerReinforceCountryRequestEvent (countryName, 1));
  }

  @Handler
  void onEvent (final PlayerReinforceCountrySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    beginReinforcementEvent = null;
  }

  @Handler
  void onEvent (final PlayerReinforceCountryDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Error. Could not reinforce {} with {}. Reason: {}.", event.getOriginalRequest ().getCountryName (),
              Strings.pluralize (event.getOriginalRequest ().getReinforcementCount (), "army", "armies"),
              event.getReason ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        // Roll back preemptive play map changes (server denied request).
        playMap.changeArmiesBy (-1, event.getOriginalRequest ().getCountryName ());
      }
    });

    eventBus.publish (StatusMessageEventGenerator.create (Strings
            .format ("Whoops, you aren't authorized to reinforce {}.", event.getOriginalRequest ().getCountryName ())));

    askPlayerToChooseACountry (event.getPlayerName ());
  }

  private void askPlayerToChooseACountry (final String playerName)
  {
    eventBus.publish (StatusMessageEventGenerator
            .create (Strings.format ("{}, choose a country to reinforce.", playerName)));
  }

  private Result <String> checkRequestExistsFor (final SelectCountryEvent event)
  {
    if (beginReinforcementEvent == null)
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
    assert beginReinforcementEvent != null;

    if (beginReinforcementEvent.isNotPlayerOwnedCountry (event.getCountryName ()))
    {
      final String failureMessage = Strings
              .format ("Ignoring [{}] because not a valid response to [{}] (Player [{}] does not own country [{}].)",
                       event, beginReinforcementEvent, beginReinforcementEvent.getPlayer (), event.getCountryName ());
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkCanReinforceCountry (final String countryName)
  {
    assert beginReinforcementEvent != null;

    if (!beginReinforcementEvent.canReinforceCountryWithSingleArmy (countryName))
    {
      final String failureMessage = Strings.format (
                                                    "Cannot reinforce country [{}] because it already contains the "
                                                            + "maximum number of armies: [{}].",
                                                    countryName, beginReinforcementEvent.getMaxArmiesPerCountry ());
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }
}
