/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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
import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerReinforceCountryEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class ReinforcementPhaseHandler extends AbstractGamePhaseHandler
{
  private final ReinforcementDialog reinforcementDialog;
  @Nullable
  private PlayerReinforceCountryEvent serverInformEvent;
  @Nullable
  private ReinforceTrigger trigger;

  enum ReinforceTrigger
  {
    REINFORCEMENTS_DIALOG_SUBMITTED,
    COUNTRY_LEFT_CLICKED
  }

  public ReinforcementPhaseHandler (final PlayMap playMap,
                                    final ReinforcementDialog reinforcementDialog,
                                    final MBassador <Event> eventBus)
  {
    super (playMap, eventBus);

    Arguments.checkIsNotNull (reinforcementDialog, "reinforcementDialog");

    this.reinforcementDialog = reinforcementDialog;
  }

  @Override
  public ImmutableSet <GamePhase> getPhases ()
  {
    return ImmutableSet.of (GamePhase.INITIAL_REINFORCEMENT, GamePhase.REINFORCEMENT);
  }

  @Override
  public void execute ()
  {
    // This method is only called for ReinforcementDialog submission.

    final String countryName = reinforcementDialog.getCountryName ();
    final int reinforcements = reinforcementDialog.getReinforcements ();

    if (checkServerInformEventExistsForCountry (countryName).failed ())
    {
      reinforcementDialog.rollbackAnyPreemptiveUpdates ();
      return;
    }

    if (checkCanReinforceCountryWithArmies (countryName, reinforcements).failed ())
    {
      reinforcementDialog.rollbackAnyPreemptiveUpdates ();
      return;
    }

    trigger = ReinforceTrigger.REINFORCEMENTS_DIALOG_SUBMITTED;

    publish (new PlayerReinforceCountryRequestEvent (countryName, reinforcements));
  }

  @Override
  public void cancel ()
  {
    // This method is only called for ReinforcementDialog cancellation.

    reinforcementDialog.rollbackAnyPreemptiveUpdates ();
  }

  @Override
  void onCountryLeftClicked (final String countryName, final float x, final float y)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (reinforcementDialog.isShown ())
    {
      reinforcementDialog.hide ();
      reinforcementDialog.rollbackAnyPreemptiveUpdates ();
      return;
    }

    if (checkServerInformEventExistsForCountry (countryName).failed ()) return;
    if (checkCountryIsReinforceable (countryName).failed ()) return;
    if (checkCanReinforceCountryWithMinArmies (countryName).failed ()) return;

    assert serverInformEvent != null;

    trigger = ReinforceTrigger.COUNTRY_LEFT_CLICKED;

    preemptivelyUpdatePlayMap (countryName, serverInformEvent.getMinReinforcementsPlacedPerCountry ());
    publish (new PlayerReinforceCountryRequestEvent (countryName,
            serverInformEvent.getMinReinforcementsPlacedPerCountry ()));
  }

  @Override
  void onCountryRightClicked (final String countryName, final float x, final float y)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (reinforcementDialog.isShown ())
    {
      reinforcementDialog.hide ();
      reinforcementDialog.rollbackAnyPreemptiveUpdates ();
    }

    if (checkServerInformEventExistsForCountry (countryName).failed ()) return;
    if (checkCountryIsReinforceable (countryName).failed ()) return;

    assert serverInformEvent != null;

    reinforcementDialog.show (serverInformEvent.getMinReinforcementsPlacedPerCountry (),
                              serverInformEvent.getTotalReinforcements (), getCountryWithName (countryName), x, y,
                              getSelfPlayerName ());
  }

  @Override
  void onNonCountryLeftClicked (final float x, final float y)
  {
    if (!reinforcementDialog.isShown ()) return;

    reinforcementDialog.hide ();
    reinforcementDialog.rollbackAnyPreemptiveUpdates ();
  }

  @Override
  void onNonCountryRightClicked (final float x, final float y)
  {
    if (!reinforcementDialog.isShown ()) return;

    reinforcementDialog.hide ();
    reinforcementDialog.rollbackAnyPreemptiveUpdates ();
  }

  @Override
  public void reset ()
  {
    super.reset ();
    serverInformEvent = null;
    trigger = null;
  }

  @Handler
  void onEvent (final PlayerReinforceCountryEvent event)
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

    if (!isSelf (event.getPerson ())) return;

    reset ();
  }

  @Handler
  void onEvent (final PlayerReinforceCountryDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.error ("Error. Could not reinforce {} with {}. Reason: {}.", event.getDeniedRequest ().getCountryName (),
               Strings.pluralize (event.getDeniedRequest ().getReinforcementCount (), "army", "armies"),
               event.getReason ());

    final PlayerReinforceCountryRequestEvent originalRequest = event.getDeniedRequest ();
    rollBackPreemptiveUpdates (originalRequest.getCountryName (), originalRequest.getReinforcementCount ());
    reset ();
  }

  private Result <String> checkServerInformEventExistsForCountry (final String countryName)
  {
    if (serverInformEvent == null)
    {
      // @formatter:off
      final String failureMessage =
              Strings.format ("Not reinforcing country [{}] because did not receive server inform event [{}].",
                              countryName, PlayerReinforceCountryEvent.class.getSimpleName ());
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
      final String failureMessage = Strings.format ("Not reinforcing country [{}] because not a valid response to [{}]",
                                                    countryName, serverInformEvent);
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

  private void rollBackPreemptiveUpdates (final String countryName, final int reinforcements)
  {
    assert trigger != null;

    // Make a defensive copy because it will be reset to null before the Runnable is executed.
    final ReinforceTrigger trigger = this.trigger;

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        switch (trigger)
        {
          case REINFORCEMENTS_DIALOG_SUBMITTED:
          {
            reinforcementDialog.rollbackAnyPreemptiveUpdates ();
            break;
          }
          case COUNTRY_LEFT_CLICKED:
          {
            changeCountryArmiesBy (-reinforcements, countryName);
          }
        }
      }
    });
  }
}
