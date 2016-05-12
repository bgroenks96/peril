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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.forerunnergames.peril.client.events.SelectCountryEvent;
import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerAttackCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerEndAttackPhaseResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerAttackCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerAttackCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AttackPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (AttackPhaseHandler.class);
  private final PlayerBox playerBox;
  private final AttackDialog attackDialog;
  private final OccupationDialog occupationDialog;
  private final Dialog battleResultDialog;
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  @Nullable
  private PlayerAttackCountryRequestEvent request = null;
  @Nullable
  private PlayerAttackCountryResponseRequestEvent response = null;
  @Nullable
  private String attackFromCountryName = null;
  @Nullable
  private String attackToCountryName = null;

  public AttackPhaseHandler (final PlayMap playMap,
                             final PlayerBox playerBox,
                             final AttackDialog attackDialog,
                             final OccupationDialog occupationDialog,
                             final Dialog battleResultDialog,
                             final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (attackDialog, "attackDialog");
    Arguments.checkIsNotNull (occupationDialog, "occupationDialog");
    Arguments.checkIsNotNull (battleResultDialog, "battleResultDialog");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.playerBox = playerBox;
    this.attackDialog = attackDialog;
    this.occupationDialog = occupationDialog;
    this.battleResultDialog = battleResultDialog;
    this.eventBus = eventBus;
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  public void onBattle ()
  {
    if (request == null)
    {
      log.warn ("Not sending response [{}] because no prior corresponding {} was received.",
                PlayerAttackCountryResponseRequestEvent.class.getSimpleName (),
                PlayerAttackCountryRequestEvent.class.getSimpleName ());
      status ("Whoops, it looks like you aren't authorized to attack.");
      return;
    }

    final String attackingCountryName = attackDialog.getAttackingCountryName ();

    if (!attackingCountryName.equals (attackFromCountryName))
    {
      log.warn ("Not sending response [{}] because specified attacking country name [{}] does not match the "
                        + "\"attack from\" name [{}] of the original request [{}].",
                PlayerAttackCountryResponseRequestEvent.class.getSimpleName (), attackingCountryName,
                attackFromCountryName, request);
      status ("Whoops, it looks like you aren't authorized to attack from {}.", attackingCountryName);
      return;
    }

    final String defendingCountryName = attackDialog.getDefendingCountryName ();

    if (!defendingCountryName.equals (attackToCountryName))
    {
      log.warn ("Not sending response [{}] because specified defending country name [{}] does not match the "
                        + "\"attack to \" country name [{}] of the original request [{}].",
                PlayerAttackCountryResponseRequestEvent.class.getSimpleName (), defendingCountryName,
                attackToCountryName, request);
      status ("Whoops, it looks like you aren't authorized to attack {}.", defendingCountryName);
      return;
    }

    response = new PlayerAttackCountryResponseRequestEvent (attackingCountryName, defendingCountryName,
            attackDialog.getActiveAttackerDieCount ());

    eventBus.publish (response);

    status ("Waiting for battle result...");
  }

  public void onRetreat ()
  {
    if (request == null)
    {
      log.warn ("Ignoring retreat because no prior {} was received.",
                PlayerAttackCountryRequestEvent.class.getSimpleName ());
      status ("Whoops, it looks like you aren't authorized to attack or retreat.");
      return;
    }

    status ("You stopped attacking {} in {} from {}.", attackDialog.getDefendingPlayerName (),
            attackDialog.getDefendingCountryName (), attackDialog.getAttackingCountryName ());

    softReset ();
  }

  public void onEndAttackPhase ()
  {
    if (request == null)
    {
      log.warn ("Ignoring ending attack phase because no prior {} was received.",
                PlayerAttackCountryRequestEvent.class.getSimpleName ());
      status ("Whoops, it looks like you aren't authorized to end an Attack Phase.");
      return;
    }

    eventBus.publish (new PlayerEndAttackPhaseResponseRequestEvent ());

    reset ();
  }

  public void reset ()
  {
    request = null;
    response = null;
    attackFromCountryName = null;
    attackToCountryName = null;
  }

  public void softReset ()
  {
    response = null;
    attackFromCountryName = null;
    attackToCountryName = null;

    askPlayerToSelectAttackFromCountry ();
  }

  public void onOccupy ()
  {
    eventBus.publish (new PlayerOccupyCountryResponseRequestEvent (occupationDialog.getDeltaArmyCount ()));
  }

  @Handler
  void onEvent (final PlayerAttackCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (request != null)
    {
      log.warn ("Ignoring [{}] because another attack is still in progress [{}]", event, request);
      return;
    }

    request = event;

    if (!attackDialog.isShown ())
    {
      askPlayerToSelectAttackFromCountry ();
      return;
    }

    attackFromCountryName = attackDialog.getAttackingCountryName ();
    attackToCountryName = attackDialog.getDefendingCountryName ();

    attackDialog.startBattle ();
  }

  @Handler
  void onEvent (final SelectCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String countryName = event.getCountryName ();

    if (checkRequestExistsFor (event).failed ()) return;

    if (isSelectingAttackFromCountry () && checkCanAttackFromCountry (countryName).succeeded ())
    {
      selectAttackFromCountry (event.getCountryName ());
      askPlayerToSelectAttackToCountry ();
    }
    else if (isSelectingAttackToCountry () && checkCanAttackToCountry (countryName).succeeded ())
    {
      selectAttackToCountry (event.getCountryName ());
      showAttackDialog ();
    }
  }

  @Handler
  void onEvent (final PlayerAttackCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (response == null)
    {
      log.warn ("Ignoring [{}] because no prior corresponding {} was sent.", event,
                PlayerAttackCountryResponseRequestEvent.class.getSimpleName ());
      return;
    }

    attackDialog.showBattleResult (event.getBattleResult ());

    reset ();
  }

  @Handler
  void onEvent (final PlayerAttackCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Could not attack country. Reason: {}", event.getReason ());

    statusOn (hasSelectedAttackFromCountry () && hasSelectedAttackToCountry (),
              "Whoops, it looks like you aren't authorized to attack from {} to {}.", attackFromCountryName,
              attackToCountryName);

    reset ();
  }

  @Handler
  void onEvent (final PlayerOccupyCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (request == null)
    {
      log.warn ("Ignoring [{}] because no prior corresponding request [{}] was received.", event,
                PlayerAttackCountryRequestEvent.class.getSimpleName ());
      return;
    }

    if (response == null)
    {
      log.warn ("Ignoring [{}] because no prior corresponding response [{}] was sent.", event,
                PlayerAttackCountryResponseRequestEvent.class.getSimpleName ());
      return;
    }

    final String sourceCountryName = event.getSourceCountryName ();

    if (!playMap.existsCountryWithName (sourceCountryName))
    {
      log.error ("Not showing {} for request [{}] because source country [{}] does not exist.",
                 OccupationDialog.class.getSimpleName (), event, sourceCountryName);
      status ("Whoops, it looks like {} doesn't exist on this map, so it can't be occupied", sourceCountryName);
      return;
    }

    final String destinationCountryName = event.getDestinationCountryName ();

    if (!playMap.existsCountryWithName (destinationCountryName))
    {
      log.error ("Not showing {} for request [{}] because destination country [{}] does not exist.",
                 OccupationDialog.class.getSimpleName (), event, destinationCountryName);
      status ("Whoops, it looks like {} doesn't exist on this map, so it can't be occupied.", destinationCountryName);
      return;
    }

    attackDialog.hide ();

    playMap.setCountryState (destinationCountryName, playMap.getPrimaryImageStateOf (sourceCountryName));

    occupationDialog.show (event.getMinOccupationArmyCount (), event.getMaxOccupationArmyCount (),
                           playMap.getCountryWithName (sourceCountryName),
                           playMap.getCountryWithName (destinationCountryName), event.getTotalArmyCount ());

    battleResultDialog.setTitle ("Victory");
    battleResultDialog.setMessage (new DefaultMessage ("General, you conquered " + destinationCountryName
            + "!\nWe must now occupy it quickly."));
    battleResultDialog.show ();
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String sourceCountryName = event.getSourceCountryName ();
    final String destinationCountryName = event.getDestinationCountryName ();
    final int deltaArmyCount = event.getDeltaArmyCount ();

    if (!occupationDialog.getSourceCountryName ().equals (sourceCountryName))
    {
      log.warn ("{} source country name [{}] does not match source country name [{}] from event [{}].",
                OccupationDialog.class.getSimpleName (), occupationDialog.getSourceCountryName (), sourceCountryName,
                event);
    }

    if (!occupationDialog.getDestinationCountryName ().equals (destinationCountryName))
    {
      log.warn ("{} destination country name [{}] does not match destination country name [{}] from event [{}].",
                OccupationDialog.class.getSimpleName (), occupationDialog.getDestinationCountryName (),
                destinationCountryName, event);
    }

    if (occupationDialog.getDeltaArmyCount () != deltaArmyCount)
    {
      log.warn ("{} delta army count [{}] does not match delta army count [{}] from event [{}].",
                OccupationDialog.class.getSimpleName (), occupationDialog.getDeltaArmyCount (), deltaArmyCount, event);
    }
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Could not occupy country. Reason: {}", event.getReason ());

    statusOn (hasSelectedAttackFromCountry () && hasSelectedAttackToCountry (),
              "Whoops, it looks like you aren't authorized to attack {} from {}.", attackFromCountryName,
              attackToCountryName);

    reset ();
  }

  private Result <String> checkRequestExistsFor (final SelectCountryEvent event)
  {
    if (request == null)
    {
      final String failureMessage = Strings
              .format ("Ignoring [{}] because no prior corresponding attack request was received.", event);
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private void askPlayerToSelectAttackFromCountry ()
  {
    assert isSelectingAttackFromCountry ();

    status ("{}, choose a country to attack from.", request.getPlayerName ());
  }

  private void askPlayerToSelectAttackToCountry ()
  {
    assert isSelectingAttackToCountry ();

    status ("{}, choose a country to attack from {}.", request.getPlayerName (), attackFromCountryName);
  }

  private boolean isSelectingAttackFromCountry ()
  {
    return request != null && attackFromCountryName == null;
  }

  private boolean isSelectingAttackToCountry ()
  {
    return hasSelectedAttackFromCountry () && attackToCountryName == null;
  }

  private void selectAttackFromCountry (final String countryName)
  {
    assert request != null;
    attackFromCountryName = countryName;
  }

  private void selectAttackToCountry (final String countryName)
  {
    assert request != null;
    attackToCountryName = countryName;
  }

  private Result <String> checkCanAttackFromCountry (final String countryName)
  {
    if (!canAttackFromCountry (countryName))
    {
      final String failureMessage = Strings.format ("Cannot attack from country [{}].", countryName);
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    attackFromCountryName = countryName;

    return Result.success ();
  }

  private Result <String> checkCanAttackToCountry (final String countryName)
  {
    if (!canAttackToCountry (countryName))
    {
      final String failureMessage = Strings.format ("Cannot attack country [{}].", countryName);
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private boolean canAttackFromCountry (final String countryName)
  {
    assert request != null;
    return request.isValidAttackFromCountry (countryName);
  }

  private boolean canAttackToCountry (final String toCountryName)
  {
    assert request != null;
    return request.isValidAttackVector (attackFromCountryName, toCountryName);
  }

  private boolean hasSelectedAttackFromCountry ()
  {
    return request != null && attackFromCountryName != null;
  }

  private boolean hasSelectedAttackToCountry ()
  {
    return request != null && attackToCountryName != null;
  }

  private void showAttackDialog ()
  {
    assert hasSelectedAttackFromCountry ();
    assert hasSelectedAttackToCountry ();

    final String attackingCountryName = attackFromCountryName;
    final String defendingCountryName = attackToCountryName;

    if (!playMap.existsCountryWithName (attackingCountryName))
    {
      log.error ("Not showing {} for request [{}] because attacking country [{}] does not exist.",
                 AttackDialog.class.getSimpleName (), request, attackingCountryName);
      status ("Whoops, it looks like {} doesn't exist on this map.", attackingCountryName);
      softReset ();
      return;
    }

    if (!playMap.existsCountryWithName (defendingCountryName))
    {
      log.error ("Not showing {} for request [{}] because defending country [{}] does not exist.",
                 AttackDialog.class.getSimpleName (), request, defendingCountryName);
      status ("Whoops, it looks like {} doesn't exist on this map.", defendingCountryName);
      softReset ();
      return;
    }

    final CountryPrimaryImageState attackingCountryState = playMap.getPrimaryImageStateOf (attackingCountryName);
    final CountryPrimaryImageState defendingCountryState = playMap.getPrimaryImageStateOf (defendingCountryName);

    assert attackingCountryState != null;
    assert defendingCountryState != null;

    if (!PlayerColor.isValidValue (attackingCountryState.name ()))
    {
      log.error ("Not showing {} for request [{}] because attacking country state [{}] is not a valid {}.",
                 AttackDialog.class.getSimpleName (), request, attackingCountryState,
                 PlayerColor.class.getSimpleName ());
      status ("Whoops, it looks like you don't own {}.", attackingCountryName);
      softReset ();
    }

    if (!PlayerColor.isValidValue (defendingCountryState.name ()))
    {
      log.error ("Not showing {} for request [{}] because defending country state [{}] is not a valid {}.",
                 AttackDialog.class.getSimpleName (), request, defendingCountryState,
                 PlayerColor.class.getSimpleName ());
      status ("Whoops, it looks like no one owns {}.", defendingCountryName);
      softReset ();
    }

    final PlayerColor attackerColor = PlayerColor.valueOf (attackingCountryState.name ());
    final PlayerColor defenderColor = PlayerColor.valueOf (defendingCountryState.name ());
    final Optional <String> attackingPlayerName = playerBox.getNameOfPlayerWithColor (attackerColor);
    final Optional <String> defendingPlayerName = playerBox.getNameOfPlayerWithColor (defenderColor);
    final Country attackingCountry = playMap.getCountryWithName (attackingCountryName);
    final Country defendingCountry = playMap.getCountryWithName (defendingCountryName);

    if (!attackingPlayerName.isPresent ())
    {
      log.error ("Not showing {} for request [{}] because attacking player either 1) does not exist or "
                         + "2) does not own attacking country [{}].", AttackDialog.class.getSimpleName (), request,
                 attackingCountry);
      status ("Whoops, it looks like you don't own {}.", attackingCountryName);
      softReset ();
      return;
    }

    if (!attackingPlayerName.get ().equals (request.getPlayerName ()))
    {
      log.warn ("Not showing {} for request [{}] because attacking player name [{}] as discovered by {} & {} does "
                        + "not match the attacking player name [{}] from the original request.",
                AttackDialog.class.getSimpleName (), request, attackingPlayerName.get (),
                PlayMap.class.getSimpleName (), PlayerBox.class.getSimpleName (), request.getPlayerName ());
      status ("Whoops, it looks like you don't own {}.", attackingCountryName);
      softReset ();
      return;
    }

    if (!defendingPlayerName.isPresent ())
    {
      log.error ("Ignoring [{}] because defending player either 1) does not exist or 2) does not own defending "
              + "country [{}].", request, defendingCountry);
      status ("Whoops, it looks like you don't own {}.", defendingCountryName);
      softReset ();
      return;
    }

    status ("{}, prepare to attack {} in {} from {}!", attackingPlayerName.get (), defendingPlayerName.get (),
            defendingCountryName, attackingCountryName);

    attackDialog.show (attackingCountry, defendingCountry, attackingPlayerName.get (), defendingPlayerName.get ());
    attackDialog.startBattle ();
  }

  private void status (final String statusMessage)
  {
    eventBus.publish (StatusMessageEventFactory.create (statusMessage));
  }

  private void status (final String statusMessage, final Object... args)
  {
    eventBus.publish (StatusMessageEventFactory.create (statusMessage, args));
  }

  private void statusOn (final boolean condition, final String statusMessage)
  {
    if (!condition) return;
    status (statusMessage);
  }

  private void statusOn (final boolean condition, final String statusMessage, final Object... args)
  {
    if (!condition) return;
    status (statusMessage, args);
  }
}
