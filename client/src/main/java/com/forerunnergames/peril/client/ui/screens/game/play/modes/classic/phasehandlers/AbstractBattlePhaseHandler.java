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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.BattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.net.events.client.interfaces.BattleRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleResultEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleSetupEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractBattlePhaseHandler implements BattlePhaseHandler
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final BattleDialog battleDialog;
  private final BattleResultDialog resultDialog;
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  @Nullable
  private BattleResultEvent lastResultEvent;
  @Nullable
  private PlayerPacket selfPlayer;

  AbstractBattlePhaseHandler (final PlayMap playMap,
                              final BattleDialog battleDialog,
                              final BattleResultDialog resultDialog,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (battleDialog, "battleDialog");
    Arguments.checkIsNotNull (resultDialog, "resultDialog");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.battleDialog = battleDialog;
    this.resultDialog = resultDialog;
    this.eventBus = eventBus;
  }

  @Override
  public final void onBattle ()
  {
    eventBus.publish (createBattleRequestEvent (battleDialog.getActiveDieCount ()));
  }

  @Override
  public void onResultAttackerVictorious (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    assert result.outcomeIs (BattleOutcome.ATTACKER_VICTORIOUS);

    resultDialog.show (result);
  }

  @Override
  public void onResultAttackerDefeated (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    assert result.outcomeIs (BattleOutcome.ATTACKER_DEFEATED);

    resultDialog.show (result);
  }

  @Override
  public void onRetreat ()
  {
    eventBus.publish (new PlayerOrderRetreatRequestEvent ());
    reset ();
  }

  @Override
  public final void onEndBattlePhase ()
  {
    eventBus.publish (new PlayerEndAttackPhaseRequestEvent ());
    reset ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void reset ()
  {
    battleDialog.hide ();
    lastResultEvent = null;
  }

  @Override
  public final void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  @Override
  public void setSelfPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    selfPlayer = player;
  }

  protected abstract BattleRequestEvent createBattleRequestEvent (final int dieCount);

  @Handler
  final void onEvent (final BattleSetupEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (isContinuingBattle (event))
    {
      battleDialog.continueBattle (event.getAttackerDieRange (), event.getDefenderDieRange ());
    }
    else
    {
      battleDialog.startBattle (event.getAttacker (), event.getDefender (),
                                playMap.getCountryWithName (event.getAttackingCountryName ()),
                                playMap.getCountryWithName (event.getDefendingCountryName ()));
    }
  }

  @Handler
  final void onEvent (final BattleResultEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (!isSelf (event.getPlayer ()))
    {
      log.debug ("Ignoring event [{}] because does not pertain to player [{}].", event, selfPlayer);
      return;
    }

    lastResultEvent = event;

    battleDialog.showBattleResult (event.getBattleResult ());
  }

  // TODO Uncomment after PERIL-816 is completed by Brian Groenke.
  // @Handler
  // final void onEvent (final PlayerOrderRetreatDeniedEvent event)
  // {
  // Arguments.checkIsNotNull (event, "event");
  //
  // log.debug ("Event received [{}].", event);
  // log.warn ("Could not retreat. Reason: {}", event.getReason ());
  // }

  private boolean isContinuingBattle (final BattleSetupEvent event)
  {
    return battleDialog.isShown () && lastResultEvent != null
            && lastResultEvent.battleOutcomeIs (BattleOutcome.CONTINUE)
            && lastResultEvent.getAttackingCountry ().equals (event.getAttackingCountry ())
            && lastResultEvent.getDefendingCountry ().equals (event.getDefendingCountry ())
            && lastResultEvent.getAttackingPlayer ().equals (event.getAttackingPlayer ())
            && lastResultEvent.getDefendingPlayer ().equals (event.getDefendingPlayer ());
  }

  private boolean isSelf (final PlayerPacket player)
  {
    return selfPlayer != null && player.is (selfPlayer);
  }
}
