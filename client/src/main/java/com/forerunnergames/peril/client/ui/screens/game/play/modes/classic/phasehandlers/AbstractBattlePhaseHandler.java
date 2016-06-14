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

import com.forerunnergames.peril.client.events.BattleDialogResetCompleteEvent;
import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerEndAttackPhaseResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleResultEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerAttackCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import com.google.common.base.Optional;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractBattlePhaseHandler implements BattlePhaseHandler
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final PlayerBox playerBox;
  private final BattleDialog battleDialog;
  private final MBassador <Event> eventBus;
  private final AtomicBoolean isBattleQueued = new AtomicBoolean (false);
  private PlayMap playMap;
  @Nullable
  private PlayerInputRequestEvent request;
  @Nullable
  private ResponseRequestEvent response;

  AbstractBattlePhaseHandler (final PlayMap playMap,
                              final PlayerBox playerBox,
                              final BattleDialog battleDialog,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (battleDialog, "battleDialog");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.playerBox = playerBox;
    this.battleDialog = battleDialog;
    this.eventBus = eventBus;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void onBattle ()
  {
    if (request == null)
    {
      log.warn ("Not sending response [{}] because no prior corresponding {} was received.",
                getBattleResponseRequestClassName (), getBattleRequestClassName ());
      status ("Whoops, it looks like you aren't authorized to {}.", attackOrDefend ());
      return;
    }

    response = createResponse (battleDialog.getAttackingCountryName (), battleDialog.getDefendingCountryName (),
                               getBattlingDieCount (battleDialog.getActiveAttackerDieCount (),
                                                    battleDialog.getActiveDefenderDieCount ()));

    eventBus.publish (response);

    status ("Waiting for battle result...");
  }

  @Override
  public final void onRetreat ()
  {
    if (request == null)
    {
      log.warn ("Ignoring retreat because no prior {} was received.", getBattleRequestClassName ());
      status ("Whoops, it looks like you aren't authorized to retreat.");
      return;
    }

    onRetreatSuccess ();
    softReset ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void onEndBattlePhase ()
  {
    if (request == null)
    {
      log.warn ("Ignoring ending attack phase because no prior {} was received.",
                PlayerAttackCountryRequestEvent.class.getSimpleName ());
      status ("Whoops, it looks like you aren't authorized to end Attack Phase.");
      return;
    }

    eventBus.publish (new PlayerEndAttackPhaseResponseRequestEvent ());

    reset ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void reset ()
  {
    request = null;
    response = null;
    battleDialog.hide ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void softReset ()
  {
    response = null;
    battleDialog.hide ();
  }

  @Override
  public final void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  protected abstract int getBattlingDieCount (final int attackerDieCount, final int defenderDieCount);

  protected abstract String attackOrDefend ();

  protected abstract String getBattleRequestClassName ();

  protected abstract String getBattleResponseRequestClassName ();

  protected abstract ResponseRequestEvent createResponse (final String attackingCountry,
                                                          final String defendingCountry,
                                                          final int dieCount);

  protected abstract void onNewBattleRequest ();

  protected abstract void onBattleStart ();

  final void onEvent (final PlayerInputRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    request = event;

    if (battleDialog.isResetting ())
    {
      isBattleQueued.set (true);
      log.debug ("Queuing new battle request [{}] until {} reset is complete.", event,
                 battleDialog.getClass ().getSimpleName ());
      return;
    }

    battle ();
  }

  @Handler
  final void onEvent (final BattleDialogResetCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (isBattleQueued.getAndSet (false))
    {
      battle ();
      return;
    }

    reset ();
  }

  final void onEvent (final BattleResultEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (response == null)
    {
      log.warn ("Ignoring [{}] because no prior corresponding {} was sent.", event,
                getBattleResponseRequestClassName ());
      return;
    }

    battleDialog.showBattleResult (event.getBattleResult ());
  }

  final void onEvent (final PlayerResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Could not {}. Reason: {}", attackOrDefend (), event.getReason ());

    reset ();
  }

  // TODO This is a hack until the core battle API redesign is complete.
  final void showBattleDialog (final String attackingCountryName, final String defendingCountryName)
  {
    Arguments.checkIsNotNull (attackingCountryName, "attackingCountryName");
    Arguments.checkIsNotNull (defendingCountryName, "defendingCountryName");

    final Optional <String> attackingPlayerName = resolveOwnerName (attackingCountryName);
    final Optional <String> defendingPlayerName = resolveOwnerName (defendingCountryName);

    if (!attackingPlayerName.isPresent ())
    {
      log.error ("Not showing {} for request [{}]. Could not resolve owner of attacking country [{}].",
                 battleDialog.getClass ().getSimpleName (), request, attackingCountryName);
      status ("Whoops, it looks like the attacker doesn't own {}.", attackingCountryName);
      softReset ();
      return;
    }

    if (!defendingPlayerName.isPresent ())
    {
      log.error ("Not showing {} for request [{}]. Could not resolve owner of defending country [{}].",
                 battleDialog.getClass ().getSimpleName (), request, defendingCountryName);
      status ("Whoops, it looks like the defender doesn't own {}.", defendingCountryName);
      softReset ();
      return;
    }

    showBattleDialog (attackingCountryName, defendingCountryName, attackingPlayerName.get (),
                      defendingPlayerName.get ());
  }

  final void showBattleDialog (final String attackingCountryName,
                               final String defendingCountryName,
                               final String attackingPlayerName,
                               final String defendingPlayerName)
  {
    Arguments.checkIsNotNull (attackingCountryName, "attackingCountryName");
    Arguments.checkIsNotNull (defendingCountryName, "defendingCountryName");
    Arguments.checkIsNotNull (attackingPlayerName, "attackingPlayerName");
    Arguments.checkIsNotNull (defendingPlayerName, "defendingPlayerName");

    if (request == null)
    {
      log.error ("Not showing {} because no prior request [{}] was received.",
                 battleDialog.getClass ().getSimpleName (), getBattleRequestClassName ());
      return;
    }

    if (!playMap.existsCountryWithName (attackingCountryName))
    {
      log.error ("Not showing {} for request [{}] because attacking country [{}] does not exist.",
                 battleDialog.getClass ().getSimpleName (), request, attackingCountryName);
      status ("Whoops, it looks like {} doesn't exist on this map.", attackingCountryName);
      softReset ();
      return;
    }

    if (!playMap.existsCountryWithName (defendingCountryName))
    {
      log.error ("Not showing {} for request [{}] because defending country [{}] does not exist.",
                 battleDialog.getClass ().getSimpleName (), request, defendingCountryName);
      status ("Whoops, it looks like {} doesn't exist on this map.", defendingCountryName);
      softReset ();
      return;
    }

    // TODO This is a hack until the core battle API redesign is complete.
    if (request instanceof PlayerAttackCountryRequestEvent && !attackingPlayerName.equals (request.getPlayerName ()))
    {
      log.warn ("Not showing {} for request [{}] because resolved owner [{}] of attacking country [{}] is not the same "
              + "player [{}] from the original request.", battleDialog.getClass ().getSimpleName (), request,
                attackingPlayerName, attackingCountryName, request.getPlayerName ());
      status ("Whoops, it looks like the attacker doesn't own {}.", attackingCountryName);
      softReset ();
      return;
    }

    // TODO This is a hack until the core battle API redesign is complete.
    if (request instanceof PlayerDefendCountryRequestEvent
            && !defendingPlayerName.equals (((PlayerDefendCountryRequestEvent) request).getDefendingPlayerName ()))
    {
      log.warn ("Not showing {} for request [{}] because resolved owner [{}] of defending country [{}] is not the same "
              + "player [{}] from the original request.", battleDialog.getClass ().getSimpleName (), request,
                defendingPlayerName, defendingCountryName, request.getPlayerName ());
      status ("Whoops, it looks like the defender doesn't own {}.", defendingCountryName);
      softReset ();
      return;
    }

    final Country attackingCountry = playMap.getCountryWithName (attackingCountryName);
    final Country defendingCountry = playMap.getCountryWithName (defendingCountryName);

    battleDialog.show (attackingCountry, defendingCountry, attackingPlayerName, defendingPlayerName);
    battleDialog.battle ();

    onBattleStart ();
  }

  void onRetreatSuccess ()
  {
    // Empty base implementation.
  }

  final void subscribe (final Object eventBusListener)
  {
    Arguments.checkIsNotNull (eventBusListener, "eventBusListener");

    eventBus.subscribe (eventBusListener);
  }

  final void unsubscribe (final Object eventBusListener)
  {
    Arguments.checkIsNotNull (eventBusListener, "eventBusListener");

    eventBus.unsubscribe (eventBusListener);
  }

  final String getBattleDialogAttackerName ()
  {
    return battleDialog.getAttackingPlayerName ();
  }

  final String getBattleDialogDefenderName ()
  {
    return battleDialog.getDefendingPlayerName ();
  }

  final String getBattleDialogAttackingCountryName ()
  {
    return battleDialog.getAttackingCountryName ();
  }

  final String getBattleDialogDefendingCountryName ()
  {
    return battleDialog.getDefendingCountryName ();
  }

  // TODO This is a hack until the core battle API redesign is complete.
  final <T extends PlayerInputRequestEvent> T getBattleRequestAs (final Class <T> requestType)
  {
    Arguments.checkIsNotNull (requestType, "requestType");
    Preconditions.checkIsTrue (request != null, "{} does not exist.", requestType);

    return requestType.cast (request);
  }

  final void status (final String statusMessage)
  {
    eventBus.publish (StatusMessageEventFactory.create (statusMessage));
  }

  final void status (final String statusMessage, final Object... args)
  {
    eventBus.publish (StatusMessageEventFactory.create (statusMessage, args));
  }

  final void statusOn (final boolean condition, final String statusMessage)
  {
    if (!condition) return;
    status (statusMessage);
  }

  final void statusOn (final boolean condition, final String statusMessage, final Object... args)
  {
    if (!condition) return;
    status (statusMessage, args);
  }

  private void battle ()
  {
    if (battleDialog.isBattleInProgress ())
    {
      battleDialog.battle ();
      return;
    }

    onNewBattleRequest ();
  }

  // TODO This is a hack until the core battle API redesign is complete.
  private Optional <String> resolveOwnerName (final String countryName)
  {
    if (!playMap.existsCountryWithName (countryName)) return Optional.absent ();

    final CountryPrimaryImageState imageState = playMap.getPrimaryImageStateOf (countryName);
    assert imageState != null;

    if (!PlayerColor.isValidValue (imageState.name ())) return Optional.absent ();

    return playerBox.getNameOfPlayerWithColor (PlayerColor.valueOf (imageState.name ()));
  }
}
