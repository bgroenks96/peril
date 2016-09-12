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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.status.StatusMessageEventGenerator;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OccupationPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (OccupationPhaseHandler.class);
  private final OccupationDialog occupationDialog;
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  @Nullable
  private PlayerOccupyCountryRequestEvent request = null;
  @Nullable
  private PlayerOccupyCountryResponseRequestEvent response = null;
  @Nullable
  private String sourceCountryName = null;
  @Nullable
  private String destCountryName = null;

  public OccupationPhaseHandler (final PlayMap playMap,
                                 final OccupationDialog occupationDialog,
                                 final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (occupationDialog, "occupationDialog");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.occupationDialog = occupationDialog;
    this.eventBus = eventBus;
  }

  public void onOccupy ()
  {
    if (request == null)
    {
      log.warn ("Not sending response [{}] because no prior corresponding {} was received.",
                PlayerOccupyCountryResponseRequestEvent.class.getSimpleName (),
                PlayerOccupyCountryRequestEvent.class.getSimpleName ());
      eventBus.publish (StatusMessageEventGenerator.create ("Whoops, it looks like you aren't authorized to occupy."));
      return;
    }

    final String sourceCountryName = occupationDialog.getSourceCountryName ();

    if (!sourceCountryName.equals (this.sourceCountryName))
    {
      log.warn ("Not sending response [{}] because specified source country name [{}] does not match the "
              + "source country name [{}] of the original request [{}].",
                PlayerOccupyCountryResponseRequestEvent.class.getSimpleName (), sourceCountryName,
                this.sourceCountryName, request);
      eventBus.publish (StatusMessageEventGenerator
              .create ("Whoops, it looks like you aren't authorized to occupy from {}.", sourceCountryName));
      return;
    }

    final String destCountryName = occupationDialog.getDestinationCountryName ();

    if (!destCountryName.equals (this.destCountryName))
    {
      log.warn ("Not sending response [{}] because specified destination country name [{}] does not match the "
              + "destination country name [{}] of the original request [{}].",
                PlayerOccupyCountryResponseRequestEvent.class.getSimpleName (), destCountryName, this.destCountryName,
                request);
      eventBus.publish (StatusMessageEventGenerator.create ("Whoops, it looks like you aren't authorized to occupy {}.",
                                                            destCountryName));
      return;
    }

    response = new PlayerOccupyCountryResponseRequestEvent (occupationDialog.getDeltaArmyCount ());

    eventBus.publish (response);
  }

  public void reset ()
  {
    request = null;
    response = null;
    sourceCountryName = null;
    destCountryName = null;
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  @Handler
  void onEvent (final PlayerOccupyCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (request != null)
    {
      log.warn ("Ignoring [{}] because another occupation is still in progress [{}].", event, request);
      return;
    }

    request = event;
    sourceCountryName = event.getSourceCountryName ();
    destCountryName = event.getDestinationCountryName ();

    final String sourceCountryName = event.getSourceCountryName ();

    if (!playMap.existsCountryWithName (sourceCountryName))
    {
      log.error ("Not showing {} for request [{}] because source country [{}] does not exist in {}.",
                 OccupationDialog.class.getSimpleName (), event, sourceCountryName, PlayMap.class.getSimpleName ());
      eventBus.publish (StatusMessageEventGenerator.create (
                                                            "Whoops, it looks like {} doesn't exist on this map, so it can't be occupied",
                                                            sourceCountryName));
      return;
    }

    final String destinationCountryName = event.getDestinationCountryName ();

    if (!playMap.existsCountryWithName (destinationCountryName))
    {
      log.error ("Not showing {} for request [{}] because destination country [{}] does not exist in {}.",
                 OccupationDialog.class.getSimpleName (), event, destinationCountryName,
                 PlayMap.class.getSimpleName ());
      eventBus.publish (StatusMessageEventGenerator.create (
                                                            "Whoops, it looks like {} doesn't exist on this map, so it can't be occupied.",
                                                            destinationCountryName));
      return;
    }

    playMap.setCountryState (destinationCountryName, playMap.getPrimaryImageStateOf (sourceCountryName));

    occupationDialog.set (event.getMinOccupationArmyCount (), event.getDestinationCountryArmyCount (),
                          event.getMaxOccupationArmyCount (), event.getTotalArmyCount (),
                          playMap.getCountryWithName (sourceCountryName),
                          playMap.getCountryWithName (destinationCountryName));
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (request == null)
    {
      log.warn ("Not sending response [{}] because no prior corresponding {} was received.",
                PlayerOccupyCountryResponseRequestEvent.class.getSimpleName (),
                PlayerOccupyCountryRequestEvent.class.getSimpleName ());
      eventBus.publish (StatusMessageEventGenerator.create ("Whoops, it looks like you aren't authorized to occupy."));
      return;
    }

    final String sourceCountryName = event.getSourceCountryName ();
    final String destinationCountryName = event.getDestinationCountryName ();
    final int deltaArmyCount = event.getDeltaArmyCount ();

    if (!occupationDialog.getSourceCountryName ().equals (sourceCountryName))
    {
      log.error ("{} source country name [{}] does not match source country name [{}] from event [{}].",
                 OccupationDialog.class.getSimpleName (), occupationDialog.getSourceCountryName (), sourceCountryName,
                 event);
    }

    if (!occupationDialog.getDestinationCountryName ().equals (destinationCountryName))
    {
      log.error ("{} destination country name [{}] does not match destination country name [{}] from event [{}].",
                 OccupationDialog.class.getSimpleName (), occupationDialog.getDestinationCountryName (),
                 destinationCountryName, event);
    }

    if (occupationDialog.getDeltaArmyCount () != deltaArmyCount)
    {
      log.error ("{} delta army count [{}] does not match delta army count [{}] from event [{}].",
                 OccupationDialog.class.getSimpleName (), occupationDialog.getDeltaArmyCount (), deltaArmyCount, event);
    }

    reset ();
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.error ("Could not occupy country. Reason: {}", event.getReason ());

    eventBus.publish (StatusMessageEventGenerator.create (
                                                          "Whoops, it looks like you aren't authorized to occupy {} from {}.",
                                                          sourceCountryName, destCountryName));

    reset ();
  }
}
