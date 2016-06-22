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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerAttackOrderResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerBeginAttackResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.interfaces.PlayerSelectCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackOrderResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerAttackOrderRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerBeginAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerAttackOrderResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerBeginAttackResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class AttackingBattlePhaseHandler extends AbstractBattlePhaseHandler
{
  private final CountrySelectionHandler countrySelectionHandler;

  public AttackingBattlePhaseHandler (final PlayMap playMap,
                                      final PlayerBox playerBox,
                                      final AttackDialog attackDialog,
                                      final MBassador <Event> eventBus)
  {
    super (playMap, playerBox, attackDialog, eventBus);

    countrySelectionHandler = new AbstractCountrySelectionHandler ("attack", eventBus)
    {
      @Override
      public void onEnd (final String sourceCountryName, final String destCountryName)
      {
        eventBus.publish (new PlayerBeginAttackResponseRequestEvent (sourceCountryName, destCountryName));
      }
    };
  }

  @Override
  public void reset ()
  {
    super.reset ();

    countrySelectionHandler.reset ();
    unsubscribe (countrySelectionHandler);
  }

  @Override
  public void softReset ()
  {
    super.softReset ();

    countrySelectionHandler.reset ();
    countrySelectionHandler.start (getBattleRequestAs (PlayerSelectCountriesRequestEvent.class));
  }

  @Override
  protected int getBattlingDieCount (final int attackerDieCount, final int defenderDieCount)
  {
    return attackerDieCount;
  }

  @Override
  protected String attackOrDefend ()
  {
    return "attack";
  }

  @Override
  protected String getBattleRequestClassName ()
  {
    return PlayerAttackOrderRequestEvent.class.getSimpleName ();
  }

  @Override
  protected String getBattleResponseRequestClassName ()
  {
    return PlayerAttackOrderResponseRequestEvent.class.getSimpleName ();
  }

  @Override
  protected ResponseRequestEvent createBattleResponse (final int dieCount)
  {
    return new PlayerAttackOrderResponseRequestEvent (dieCount);
  }

  @Override
  protected void onNewBattleRequest ()
  {
    softReset ();
    subscribe (countrySelectionHandler);
  }

  @Override
  protected void onBattleStart ()
  {
    status ("{}, prepare to attack {} in {} from {}!", getBattleDialogAttackerName (), getBattleDialogDefenderName (),
            getBattleDialogDefendingCountryName (), getBattleDialogAttackingCountryName ());
  }

  @Override
  void onRetreatSuccess ()
  {
    status ("You stopped attacking {} in {} from {}.", getBattleDialogDefenderName (),
            getBattleDialogDefendingCountryName (), getBattleDialogAttackingCountryName ());
  }

  @Handler
  void onEvent (final PlayerBeginAttackRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    softReset ();
    subscribe (countrySelectionHandler);
  }

  @Handler
  void onEvent (final PlayerBeginAttackResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
  }

  @Handler
  void onEvent (final PlayerAttackOrderRequestEvent event)
  {
    super.onEvent (event);
  }

  @Handler
  void onEvent (final PlayerAttackOrderResponseSuccessEvent event)
  {
    super.onEvent (event);
  }

  @Handler
  void onEvent (final PlayerAttackOrderResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    status ("Whoops, it looks like you aren't authorized to attack {} in {} from {}. Reason: {}",
            getBattleDialogDefenderName (), getBattleDialogDefendingCountryName (),
            getBattleDialogAttackingCountryName (),
            Strings.toCase (event.getReason ().toString ().replaceAll ("_", " "), LetterCase.LOWER));

    super.onEvent (event);
  }
}
