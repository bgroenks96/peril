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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
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

public final class ReinforcementPhaseHandler extends AbstractGamePhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (ReinforcementPhaseHandler.class);
  @Nullable
  private PlayerBeginReinforcementEvent serverInformEvent;

  public ReinforcementPhaseHandler (final PlayMap playMap, final MBassador <Event> eventBus)
  {
    super (playMap, eventBus);
  }

  @Override
  protected void onCountryClicked (final String countryName)
  {
    if (checkServerInformEventExistsForCountryClick (countryName).failed ()) return;
    if (checkPlayerOwnsClickedCountry (countryName).failed ()) return;
    if (checkCanReinforceCountry (countryName).failed ()) return;

    preemptivelyUpdatePlayMap (countryName);
    publish (new PlayerReinforceCountryRequestEvent (countryName, 1));
    reset ();
  }

  @Override
  public void execute ()
  {
    // Empty implementation.
  }

  @Override
  public void reset ()
  {
    super.reset ();
    serverInformEvent = null;
  }

  @Handler
  void onEvent (final PlayerBeginReinforcementEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    serverInformEvent = event;

    listenForPlayMapCountryClicks ();
  }

  @Handler
  void onEvent (final PlayerReinforceCountrySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (isSelf (event.getPlayer ())) verifyPreemptivePlayMapUpdates (event);
  }

  @Handler
  void onEvent (final PlayerReinforceCountryDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.error ("Error. Could not reinforce {} with {}. Reason: {}.", event.getOriginalRequest ().getCountryName (),
               Strings.pluralize (event.getOriginalRequest ().getReinforcementCount (), "army", "armies"),
               event.getReason ());

    rollBackPreemptivePlayMapUpdates (event.getOriginalRequest ().getCountryName ());
    reset ();
  }

  private Result <String> checkServerInformEventExistsForCountryClick (final String countryName)
  {
    if (serverInformEvent == null)
    {
      // @formatter:off
      final String failureMessage =
              Strings.format ("Ignoring click on country [{}] because did not receive server inform event [{}].",
                              countryName, PlayerBeginReinforcementEvent.class.getSimpleName ());
      // @formatter:on
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkPlayerOwnsClickedCountry (final String countryName)
  {
    assert serverInformEvent != null;

    if (serverInformEvent.isNotPlayerOwnedCountry (countryName))
    {
      // @formatter:off
      final String failureMessage =
              Strings.format ("Ignoring click on country [{}] because not a valid response to [{}] (Player [{}] does not own country.)",
                              countryName, serverInformEvent, serverInformEvent.getPlayer ());
      // @formatter:on
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkCanReinforceCountry (final String countryName)
  {
    assert serverInformEvent != null;

    if (!serverInformEvent.canReinforceCountryWithSingleArmy (countryName))
    {
      // @formatter:off
      final String failureMessage =
              Strings.format ("Cannot reinforce country [{}] because it already contains the maximum number of armies: [{}].",
                              countryName, serverInformEvent.getMaxArmiesPerCountry ());
      // @formatter:on
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private void preemptivelyUpdatePlayMap (final String countryName)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        changeCountryArmiesBy (1, countryName);
      }
    });
  }

  private void verifyPreemptivePlayMapUpdates (final PlayerReinforceCountrySuccessEvent event)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        assert countryArmyCountIs (event.getCountryArmyCount (), event.getCountryName ());
      }
    });
  }

  private void rollBackPreemptivePlayMapUpdates (final String countryName)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        changeCountryArmiesBy (-1, countryName);
      }
    });
  }
}
