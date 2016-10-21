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
  private final ReinforcementsPopupMenu reinforcementsPopupMenu;
  @Nullable
  private PlayerBeginReinforcementEvent serverInformEvent;

  public ReinforcementPhaseHandler (final PlayMap playMap,
                                    final ReinforcementsPopupMenu reinforcementsPopupMenu,
                                    final MBassador <Event> eventBus)
  {
    super (playMap, eventBus);

    Arguments.checkIsNotNull (reinforcementsPopupMenu, "reinforcementsPopupMenu");

    this.reinforcementsPopupMenu = reinforcementsPopupMenu;
  }

  @Override
  public void execute ()
  {
    // This method is only called for ReinforcementsPopupMenu submission.

    final String countryName = reinforcementsPopupMenu.getCountryName ();
    final int reinforcements = reinforcementsPopupMenu.getReinforcements ();

    if (checkServerInformEventExistsForCountry (countryName).failed ()) return;
    if (checkCanReinforceCountryWithArmies (countryName, reinforcements).failed ()) return;

    publish (new PlayerReinforceCountryRequestEvent (countryName, reinforcements));
    reset ();
  }

  @Override
  public void cancel ()
  {
    // This method is only called for ReinforcementsPopupMenu cancellation.

    reinforcementsPopupMenu.cancel ();
  }

  @Override
  void onCountryLeftClicked (final String countryName, final float x, final float y)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (reinforcementsPopupMenu.isShown ())
    {
      reinforcementsPopupMenu.hide ();
      reinforcementsPopupMenu.cancel ();
      return;
    }

    if (checkServerInformEventExistsForCountry (countryName).failed ()) return;
    if (checkCountryIsReinforceable (countryName).failed ()) return;
    if (checkCanReinforceCountryWithMinArmies (countryName).failed ()) return;

    assert serverInformEvent != null;

    preemptivelyUpdatePlayMap (countryName, serverInformEvent.getMinReinforcementsPlacedPerCountry ());
    publish (new PlayerReinforceCountryRequestEvent (countryName,
            serverInformEvent.getMinReinforcementsPlacedPerCountry ()));
    reset ();
  }

  @Override
  void onCountryRightClicked (final String countryName, final float x, final float y)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (reinforcementsPopupMenu.isShown ())
    {
      reinforcementsPopupMenu.hide ();
      reinforcementsPopupMenu.cancel ();
    }

    if (checkServerInformEventExistsForCountry (countryName).failed ()) return;
    if (checkCountryIsReinforceable (countryName).failed ()) return;

    assert serverInformEvent != null;

    reinforcementsPopupMenu.show (serverInformEvent.getMinReinforcementsPlacedPerCountry (),
                                  serverInformEvent.getTotalReinforcements (), getCountryWithName (countryName), x, y,
                                  getSelfPlayer ());
  }

  @Override
  void onNonCountryLeftClicked (final float x, final float y)
  {
    if (!reinforcementsPopupMenu.isShown ()) return;

    reinforcementsPopupMenu.hide ();
    reinforcementsPopupMenu.cancel ();
  }

  @Override
  void onNonCountryRightClicked (final float x, final float y)
  {
    if (!reinforcementsPopupMenu.isShown ()) return;

    reinforcementsPopupMenu.hide ();
    reinforcementsPopupMenu.cancel ();
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

    final PlayerReinforceCountryRequestEvent originalRequest = event.getOriginalRequest ();
    rollBackPreemptivePlayMapUpdates (originalRequest.getCountryName (), originalRequest.getReinforcementCount ());
    reset ();
  }

  private Result <String> checkServerInformEventExistsForCountry (final String countryName)
  {
    if (serverInformEvent == null)
    {
      // @formatter:off
      final String failureMessage =
              Strings.format ("Not reinforcing country [{}] because did not receive server inform event [{}].",
                              countryName, PlayerBeginReinforcementEvent.class.getSimpleName ());
      // @formatter:on
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkCountryIsReinforceable (final String countryName)
  {
    assert serverInformEvent != null;

    if (!serverInformEvent.isReinforceableCountry (countryName))
    {
      final String failureMessage = Strings
              .format ("Not reinforcing country [{}] because not a valid response to [{}]", countryName,
                       serverInformEvent);
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkCanReinforceCountryWithMinArmies (final String countryName)
  {
    assert serverInformEvent != null;

    if (!serverInformEvent.canReinforceCountryWithMinArmies (countryName))
    {
      final String failureMessage = Strings
              .format ("Not reinforcing country [{}] with army count: [{}] because not a valid response to [{}].",
                       countryName, serverInformEvent.getMinReinforcementsPlacedPerCountry (), serverInformEvent);
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkCanReinforceCountryWithArmies (final String countryName, final int armies)
  {
    assert serverInformEvent != null;

    if (!serverInformEvent.canReinforceCountryWithArmies (countryName, armies))
    {
      final String failureMessage = Strings
              .format ("Not reinforcing country [{}] with army count: [{}] because not a valid response to [{}].",
                       countryName, serverInformEvent.getMinReinforcementsPlacedPerCountry (), serverInformEvent);
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private void preemptivelyUpdatePlayMap (final String countryName, final int reinforcements)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        changeCountryArmiesBy (reinforcements, countryName);
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

  private void rollBackPreemptivePlayMapUpdates (final String countryName, final int reinforcements)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        changeCountryArmiesBy (reinforcements, countryName);
      }
    });
  }
}
