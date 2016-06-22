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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerDefendCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class DefendingBattlePhaseHandler extends AbstractBattlePhaseHandler
{
  public DefendingBattlePhaseHandler (final PlayMap playMap,
                                      final PlayerBox playerBox,
                                      final DefendDialog defendDialog,
                                      final MBassador <Event> eventBus)
  {
    super (playMap, playerBox, defendDialog, eventBus);
  }

  @Override
  protected int getBattlingDieCount (final int attackerDieCount, final int defenderDieCount)
  {
    return defenderDieCount;
  }

  @Override
  protected String attackOrDefend ()
  {
    return "defend";
  }

  @Override
  protected String getBattleRequestClassName ()
  {
    return PlayerDefendCountryRequestEvent.class.getSimpleName ();
  }

  @Override
  protected String getBattleResponseRequestClassName ()
  {
    return PlayerDefendCountryResponseRequestEvent.class.getSimpleName ();
  }

  @Override
  protected ResponseRequestEvent createBattleResponse (final int dieCount)
  {
    Arguments.checkLowerExclusiveBound (dieCount, 0, "dieCount");

    return new PlayerDefendCountryResponseRequestEvent (dieCount);
  }

  @Override
  protected void onNewBattleRequest ()
  {
    final PlayerDefendCountryRequestEvent request = getBattleRequestAs (PlayerDefendCountryRequestEvent.class);

    showBattleDialog (request.getAttackingCountryName (), request.getDefendingCountryName (),
                      request.getAttackingPlayerName (), request.getDefendingPlayerName ());
  }

  @Override
  protected void onBattleStart ()
  {
    status ("{}, prepare to defend {} against {} in {}!", getBattleDialogDefenderName (),
            getBattleDialogDefendingCountryName (), getBattleDialogAttackerName (),
            getBattleDialogAttackingCountryName ());
  }

  @Handler
  void onEvent (final PlayerDefendCountryRequestEvent event)
  {
    super.onEvent (event);
  }

  @Handler
  void onEvent (final PlayerDefendCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    status ("You defended {} against {} in {}, destroying {} & losing {}!", event.getDefendingCountryName (),
            event.getAttackingPlayerName (), event.getAttackingCountryName (),
            Strings.pluralize (Math.abs (event.getAttackingCountryArmyDelta ()), "army", "armies"),
            Strings.pluralize (Math.abs (event.getDefendingCountryArmyDelta ()), "army", "armies"));

    super.onEvent (event);
  }

  @Handler
  void onEvent (final PlayerDefendCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    status ("Whoops, it looks like you aren't authorized to defend {} against {} in {}. Reason: {}",
            getBattleDialogDefendingCountryName (), getBattleDialogAttackerName (),
            getBattleDialogAttackingCountryName (),
            Strings.toCase (event.getReason ().toString ().replaceAll ("_", " "), LetterCase.LOWER));

    super.onEvent (event);
  }
}
