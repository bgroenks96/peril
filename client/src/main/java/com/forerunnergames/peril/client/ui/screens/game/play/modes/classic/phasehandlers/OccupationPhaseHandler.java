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

public final class OccupationPhaseHandler extends AbstractGamePhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (OccupationPhaseHandler.class);
  private final OccupationDialog occupationDialog;
  @Nullable
  private PlayerOccupyCountryRequestEvent request = null;
  @Nullable
  private PlayerOccupyCountryResponseRequestEvent response = null;
  @Nullable
  private String sourceCountryName = null;
  @Nullable
  private String targetCountryName = null;

  public OccupationPhaseHandler (final PlayMap playMap,
                                 final OccupationDialog occupationDialog,
                                 final MBassador <Event> eventBus)
  {
    super (playMap, eventBus);

    Arguments.checkIsNotNull (occupationDialog, "occupationDialog");

    this.occupationDialog = occupationDialog;
  }

  @Override
  public void execute ()
  {
    if (request == null)
    {
      log.error ("Not sending response [{}] because no prior corresponding {} was received.",
                 PlayerOccupyCountryResponseRequestEvent.class.getSimpleName (),
                 PlayerOccupyCountryRequestEvent.class.getSimpleName ());
      return;
    }

    final String sourceCountryName = occupationDialog.getSourceCountryName ();

    if (!sourceCountryName.equals (this.sourceCountryName))
    {
      log.error ("Not sending response [{}] because specified source country name [{}] does not match the "
              + "source country name [{}] of the original request [{}].",
                 PlayerOccupyCountryResponseRequestEvent.class.getSimpleName (), sourceCountryName,
                 this.sourceCountryName, request);
      return;
    }

    final String targetCountryName = occupationDialog.getTargetCountryName ();

    if (!targetCountryName.equals (this.targetCountryName))
    {
      log.error ("Not sending response [{}] because specified target country name [{}] does not match the "
              + "target country name [{}] of the original request [{}].",
                 PlayerOccupyCountryResponseRequestEvent.class.getSimpleName (), targetCountryName,
                 this.targetCountryName, request);
      return;
    }

    response = new PlayerOccupyCountryResponseRequestEvent (occupationDialog.getDeltaArmyCount ());

    publish (response);
  }

  @Override
  public void reset ()
  {
    super.reset ();

    request = null;
    response = null;
    sourceCountryName = null;
    targetCountryName = null;
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
    targetCountryName = event.getTargetCountryName ();

    final String sourceCountryName = event.getSourceCountryName ();

    if (!existsCountryWithName (sourceCountryName))
    {
      log.error ("Not showing {} for request [{}] because source country [{}] does not exist in {}.",
                 OccupationDialog.class.getSimpleName (), event, sourceCountryName, PlayMap.class.getSimpleName ());
      return;
    }

    final String targetCountryName = event.getTargetCountryName ();

    if (!existsCountryWithName (targetCountryName))
    {
      log.error ("Not showing {} for request [{}] because target country [{}] does not exist in {}.",
                 OccupationDialog.class.getSimpleName (), event, targetCountryName, PlayMap.class.getSimpleName ());
      return;
    }

    occupationDialog.set (event.getMinOccupationArmyCount (), event.getTargetCountryArmyCount (),
                          event.getMaxOccupationArmyCount (), event.getTotalArmyCount (),
                          getCountryWithName (sourceCountryName), getCountryWithName (targetCountryName));
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
      return;
    }

    final String sourceCountryName = event.getSourceCountryName ();
    final String targetCountryName = event.getTargetCountryName ();
    final int deltaArmyCount = event.getDeltaArmyCount ();

    if (!occupationDialog.getSourceCountryName ().equals (sourceCountryName))
    {
      log.error ("{} source country name [{}] does not match source country name [{}] from event [{}].",
                 OccupationDialog.class.getSimpleName (), occupationDialog.getSourceCountryName (), sourceCountryName,
                 event);
    }

    if (!occupationDialog.getTargetCountryName ().equals (targetCountryName))
    {
      log.error ("{} target country name [{}] does not match target country name [{}] from event [{}].",
                 OccupationDialog.class.getSimpleName (), occupationDialog.getTargetCountryName (), targetCountryName,
                 event);
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

    reset ();
  }
}
