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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.fortification.FortificationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.status.StatusMessageEventGenerator;
import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectFortifyVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginFortificationEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FortificationPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (FortificationPhaseHandler.class);
  private final CountryVectorSelectionHandler countryVectorSelectionHandler;
  private final FortificationDialog fortificationDialog;
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  @Nullable
  private PlayerBeginFortificationEvent request = null;
  @Nullable
  private PlayerSelectFortifyVectorRequestEvent response = null;

  public FortificationPhaseHandler (final PlayMap playMap,
                                    final FortificationDialog fortificationDialog,
                                    final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (fortificationDialog, "fortificationDialog");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.fortificationDialog = fortificationDialog;
    this.eventBus = eventBus;

    countryVectorSelectionHandler = new AbstractCountryVectorSelectionHandler ("maneuver", eventBus)
    {
      @Override
      public void onEnd (final String sourceCountryName, final String targetCountryName)
      {
        showFortificationDialog (sourceCountryName, targetCountryName);
      }
    };
  }

  public void onFortify ()
  {
    if (request == null)
    {
      log.warn ("Not sending response [{}] because no prior corresponding {} was received.",
                PlayerSelectFortifyVectorRequestEvent.class.getSimpleName (),
                PlayerBeginFortificationEvent.class.getSimpleName ());
      eventBus.publish (StatusMessageEventGenerator
              .create ("Whoops, it looks like you aren't authorized to perform a post-combat maneuver."));
      softReset ();
      return;
    }

    // FIXME ?
    response = new PlayerSelectFortifyVectorRequestEvent (fortificationDialog.getSourceCountryName (),
            fortificationDialog.getDestinationCountryName ());

    eventBus.publish (response);
  }

  public void onCancel ()
  {
    eventBus.publish (StatusMessageEventGenerator.create ("You cancelled your post-combat maneuver from {} to {}.",
                                                          fortificationDialog.getSourceCountryName (),
                                                          fortificationDialog.getDestinationCountryName ()));
    softReset ();
  }

  public void reset ()
  {
    request = null;
    response = null;
    countryVectorSelectionHandler.reset ();
    eventBus.unsubscribe (countryVectorSelectionHandler);
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  public void onEndFortificationPhase ()
  {
    eventBus.publish (new EndPlayerTurnRequestEvent ());
    reset ();
  }

  @Handler
  void onEvent (final PlayerBeginFortificationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (request != null)
    {
      log.warn ("Ignoring [{}] because another fortification is still in progress [{}].", event, request);
      return;
    }

    if (event.getValidVectors ().isEmpty ())
    {
      eventBus.publish (StatusMessageEventGenerator
              .create ("Skipping Post-Combat Maneuver Phase because you have no valid maneuvers."));
      onEndFortificationPhase ();
      return;
    }

    request = event;

    eventBus.subscribe (countryVectorSelectionHandler);
    softReset ();
  }

  @Handler
  void onEvent (final PlayerFortifyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String sourceCountryName = event.getSourceCountryName ();
    final String destinationCountryName = event.getTargetCountryName ();
    final int deltaArmyCount = event.getDeltaArmyCount ();

    if (!fortificationDialog.getSourceCountryName ().equals (sourceCountryName))
    {
      log.error ("{} source country name [{}] does not match source country name [{}] from event [{}].",
                 FortificationDialog.class.getSimpleName (), fortificationDialog.getSourceCountryName (),
                 sourceCountryName, event);
    }

    if (!fortificationDialog.getDestinationCountryName ().equals (destinationCountryName))
    {
      log.error ("{} destination country name [{}] does not match destination country name [{}] from event [{}].",
                 FortificationDialog.class.getSimpleName (), fortificationDialog.getDestinationCountryName (),
                 destinationCountryName, event);
    }

    if (fortificationDialog.getDeltaArmyCount () != deltaArmyCount)
    {
      log.error ("{} delta army count [{}] does not match delta army count [{}] from event [{}].",
                 FortificationDialog.class.getSimpleName (), fortificationDialog.getDeltaArmyCount (), deltaArmyCount,
                 event);
    }

    reset ();
  }

  @Handler
  void onEvent (final PlayerSelectFortifyVectorDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.error ("Could not fortify country. Reason: {}", event.getReason ());

    eventBus.publish (StatusMessageEventGenerator
            .create ("Whoops, it looks like you aren't authorized to maneuver from {} to {}. Reason: {}",
                     fortificationDialog.getSourceCountryName (), fortificationDialog.getDestinationCountryName (),
                     Strings.toCase (event.getReason ().toString ().replaceAll ("_", " "), LetterCase.LOWER)));

    reset ();
  }

  private void softReset ()
  {
    response = null;
    countryVectorSelectionHandler.reset ();
    // FIXME: countryVectorSelectionHandler.start (request);
  }

  private void showFortificationDialog (final String sourceCountryName, final String destCountryName)
  {
    if (!playMap.existsCountryWithName (sourceCountryName))
    {
      log.error ("Not showing {} for request [{}] because source country [{}] does not exist.",
                 fortificationDialog.getClass ().getSimpleName (), request, sourceCountryName);
      eventBus.publish (StatusMessageEventGenerator.create ("Whoops, it looks like {} doesn't exist on this map.",
                                                            sourceCountryName));
      softReset ();
      return;
    }

    if (!playMap.existsCountryWithName (destCountryName))
    {
      log.error ("Not showing {} for request [{}] because destination country [{}] does not exist.",
                 fortificationDialog.getClass ().getSimpleName (), request, destCountryName);
      eventBus.publish (StatusMessageEventGenerator.create ("Whoops, it looks like {} doesn't exist on this map.",
                                                            destCountryName));
      softReset ();
      return;
    }

    final Country sourceCountry = playMap.getCountryWithName (sourceCountryName);
    final Country destCountry = playMap.getCountryWithName (destCountryName);

    // TODO This is a hack until the core fortification API redesign is complete.
    final int currentDestArmies = destCountry.getArmies ();
    final int totalArmies = currentDestArmies + sourceCountry.getArmies ();
    final int minDestArmies = currentDestArmies;
    final int maxDestArmies = totalArmies - 1;

    fortificationDialog.show (minDestArmies, currentDestArmies, maxDestArmies, totalArmies, sourceCountry, destCountry);
  }
}
