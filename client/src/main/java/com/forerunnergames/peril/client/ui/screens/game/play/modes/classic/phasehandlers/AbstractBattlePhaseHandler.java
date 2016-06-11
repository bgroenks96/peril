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
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractBattlePhaseHandler implements BattlePhaseHandler
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final ExecutorService executorService = Executors.newSingleThreadExecutor ();
  private final Runnable publishResponseTask = new PublishResponseTask ();
  private final PlayerBox playerBox;
  private final BattleDialog battleDialog;
  private final BattleResetState resetState;
  private final MBassador <Event> eventBus;
  private Future <?> publishResponseFuture = Futures.immediateCancelledFuture ();
  private PlayMap playMap;
  @Nullable
  private PlayerInputRequestEvent request;
  @Nullable
  private volatile ResponseRequestEvent response;

  AbstractBattlePhaseHandler (final PlayMap playMap,
                              final PlayerBox playerBox,
                              final BattleDialog battleDialog,
                              final BattleResetState resetState,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (battleDialog, "battleDialog");
    Arguments.checkIsNotNull (resetState, "resetState");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.playerBox = playerBox;
    this.battleDialog = battleDialog;
    this.resetState = resetState;
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

    if (!publishResponseFuture.isDone ())
    {
      log.warn ("Not sending response [{}] because another response is already queued.",
                getBattleResponseRequestClassName ());
      status ("Whoops, you can't {} again while still waiting for the last battle result.", attackOrDefend ());
      return;
    }

    // TODO This is a hack until the core battle API redesign is complete.
    publishResponseFuture = executorService.submit (publishResponseTask);

    log.trace ("Queued publish battle response task.");
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void onRetreat ()
  {
    if (request == null)
    {
      log.warn ("Ignoring retreat because no prior {} was received.", getBattleRequestClassName ());
      status ("Whoops, it looks like you aren't authorized to retreat.");
      return;
    }

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
    publishResponseFuture.cancel (true);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void softReset ()
  {
    response = null;
    publishResponseFuture.cancel (true);
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

  protected abstract void onBattleStart (final String attackerName,
                                         final String defenderName,
                                         final String attackingCountryName,
                                         final String defendingCountryName);

  final void hideBattleDialog ()
  {
    battleDialog.hide ();
  }

  final void onBattleRequestEvent (final PlayerInputRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (request != null)
    {
      log.warn ("Ignoring [{}] because another battle is still in progress [{}].", event, request);
      return;
    }

    request = event;

    if (!battleDialog.isShown ())
    {
      onNewBattleRequest ();
      return;
    }

    battleDialog.startBattle ();
  }

  final void onBattleResponseSuccessEvent (final BattleResultEvent event)
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

    reset ();
  }

  final void onBattleResponseDeniedEvent (final PlayerResponseDeniedEvent event)
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
      log.error ("Not showing {} for request [{}]. Could not resolve owner of attacking country [{}].", battleDialog
              .getClass ().getSimpleName (), request, attackingCountryName);
      status ("Whoops, it looks like the attacker doesn't own {}.", attackingCountryName);
      softReset ();
      return;
    }

    if (!defendingPlayerName.isPresent ())
    {
      log.error ("Not showing {} for request [{}]. Could not resolve owner of defending country [{}].", battleDialog
              .getClass ().getSimpleName (), request, defendingCountryName);
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
      log.error ("Not showing {} because no prior request [{}] was received.", battleDialog.getClass ()
              .getSimpleName (), getBattleRequestClassName ());
      return;
    }

    if (!playMap.existsCountryWithName (attackingCountryName))
    {
      log.error ("Not showing {} for request [{}] because attacking country [{}] does not exist.", battleDialog
              .getClass ().getSimpleName (), request, attackingCountryName);
      status ("Whoops, it looks like {} doesn't exist on this map.", attackingCountryName);
      softReset ();
      return;
    }

    if (!playMap.existsCountryWithName (defendingCountryName))
    {
      log.error ("Not showing {} for request [{}] because defending country [{}] does not exist.", battleDialog
              .getClass ().getSimpleName (), request, defendingCountryName);
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
    battleDialog.startBattle ();

    onBattleStart (attackingPlayerName, defendingPlayerName, attackingCountryName, defendingCountryName);
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

  // TODO This is a hack until the core battle API redesign is complete.
  private Optional <String> resolveOwnerName (final String countryName)
  {
    if (!playMap.existsCountryWithName (countryName)) return Optional.absent ();

    final CountryPrimaryImageState imageState = playMap.getPrimaryImageStateOf (countryName);
    assert imageState != null;

    if (!PlayerColor.isValidValue (imageState.name ())) return Optional.absent ();

    return playerBox.getNameOfPlayerWithColor (PlayerColor.valueOf (imageState.name ()));
  }

  // TODO This is a hack until the core battle API redesign is complete.
  private final class PublishResponseTask implements Runnable
  {
    private final Lock lock = new ReentrantLock ();

    @Override
    public void run ()
    {
      log.trace ("Waiting for battle reset to finish...");

      try
      {
        resetState.waitForBattleResetToFinish ();
      }
      catch (final InterruptedException e)
      {
        log.trace ("Interrupted while waiting for battle reset to finish.", e);
        resetState.cancelReset ();
        log.trace ("Battle reset cancelled.");
        log.trace ("Not publishing battle response (interrupted).");
        return;
      }

      log.trace ("Battle reset finished.");

      lock.lock ();
      try
      {
        response = createResponse (battleDialog.getAttackingCountryName (),
                                   battleDialog.getDefendingCountryName (),
                                   getBattlingDieCount (battleDialog.getActiveAttackerDieCount (),
                                                        battleDialog.getActiveDefenderDieCount ()));

        if (Thread.currentThread ().isInterrupted ())
        {
          log.trace ("Not publishing battle response (interrupted).");
          response = null;
          return;
        }

        eventBus.publishAsync (response);

        log.trace ("Published battle response [{}].", response);

        status ("Waiting for battle result...");
      }
      finally
      {
        lock.unlock ();
      }
    }
  }
}
