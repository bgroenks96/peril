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
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOrderAttackDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginAttackEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueAttackOrderEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderAttackSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectAttackVectorSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class AttackingBattlePhaseHandler extends AbstractBattlePhaseHandler
{
  private final CountryVectorSelectionHandler countryVectorSelectionHandler;

  public AttackingBattlePhaseHandler (final PlayMap playMap,
                                      final PlayerBox playerBox,
                                      final AttackDialog attackDialog,
                                      final MBassador <Event> eventBus)
  {
    super (playMap, playerBox, attackDialog, eventBus);

    countryVectorSelectionHandler = new AbstractCountryVectorSelectionHandler ("attack", eventBus)
    {
      @Override
      public void onEnd (final String sourceCountryName, final String targetCountryName)
      {
        eventBus.publish (new PlayerSelectAttackVectorRequestEvent (sourceCountryName, targetCountryName));
      }
    };
  }

  @Override
  public void reset ()
  {
    super.reset ();

    countryVectorSelectionHandler.reset ();
    unsubscribe (countryVectorSelectionHandler);
  }

  @Override
  public void softReset ()
  {
    super.softReset ();

    countryVectorSelectionHandler.reset ();
    countryVectorSelectionHandler.start (getBattleRequestAs (PlayerBeginAttackEvent.class));
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
    return PlayerIssueAttackOrderEvent.class.getSimpleName ();
  }

  @Override
  protected String getBattleResponseRequestClassName ()
  {
    return PlayerOrderAttackRequestEvent.class.getSimpleName ();
  }

  @Override
  protected PlayerRequestEvent createBattleResponse (final int dieCount)
  {
    return new PlayerOrderAttackRequestEvent (dieCount);
  }

  @Override
  protected void onNewBattleRequest ()
  {
    softReset ();
    subscribe (countryVectorSelectionHandler);
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
  void onEvent (final PlayerBeginAttackEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    softReset ();
    subscribe (countryVectorSelectionHandler);
  }

  @Handler
  void onEvent (final PlayerSelectAttackVectorSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
  }

  @Handler
  void onEvent (final PlayerIssueAttackOrderEvent event)
  {
    super.onEvent (event);
  }

  @Handler
  void onEvent (final PlayerOrderAttackSuccessEvent event)
  {
    super.onEvent (event);
  }

  @Handler
  void onEvent (final PlayerOrderAttackDeniedEvent event)
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
