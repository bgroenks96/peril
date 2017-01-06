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

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.BattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.client.interfaces.BattleRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleResultEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleSetupEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

abstract class AbstractBattlePhaseHandler extends AbstractGamePhaseHandler implements BattlePhaseHandler
{
  private final BattleDialog battleDialog;
  private final BattleResultDialog resultDialog;
  @Nullable
  private BattleResultEvent lastResultEvent;

  AbstractBattlePhaseHandler (final PlayMap playMap,
                              final BattleDialog battleDialog,
                              final BattleResultDialog resultDialog,
                              final MBassador <Event> eventBus)
  {
    super (playMap, eventBus);

    Arguments.checkIsNotNull (battleDialog, "battleDialog");
    Arguments.checkIsNotNull (resultDialog, "resultDialog");

    this.battleDialog = battleDialog;
    this.resultDialog = resultDialog;
  }

  @Override
  public void onResultAttackerVictorious (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    assert result.outcomeIs (BattleOutcome.ATTACKER_VICTORIOUS);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        // Preemptively update play map & update battle dialog to match.
        setCountryOwner (result.getAttackingPlayerColor (), result.getDefendingCountryName ());
        battleDialog.updateCountries (getCountryWithName (result.getAttackingCountryName ()),
                                      getCountryWithName (result.getDefendingCountryName ()));
        resultDialog.show (result);
      }
    });
  }

  @Override
  public void onResultAttackerDefeated (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    assert result.outcomeIs (BattleOutcome.ATTACKER_DEFEATED);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        resultDialog.show (result);
      }
    });
  }

  @Override
  public final void onEndBattlePhase ()
  {
    publish (new PlayerEndAttackPhaseRequestEvent ());
    reset ();
  }

  @Override
  public ImmutableSet <GamePhase> getPhases ()
  {
    return ImmutableSet.of (GamePhase.ATTACK);
  }

  @Override
  public final void execute ()
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        publish (createBattleRequestEvent (battleDialog.getActiveDieCount ()));
      }
    });
  }

  @Override
  public void cancel ()
  {
    publish (new PlayerRetreatRequestEvent ());
    reset ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void reset ()
  {
    super.reset ();

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        battleDialog.hide ();
      }
    });

    lastResultEvent = null;
  }

  protected abstract BattleRequestEvent createBattleRequestEvent (final int dieCount);

  @Handler
  final void onEvent (final BattleSetupEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        if (isContinuingBattle (event))
        {
          battleDialog.continueBattle (event.getAttackerDieRange (), event.getDefenderDieRange ());
        }
        else
        {
          battleDialog.startBattle (event.getAttacker (), event.getDefender (),
                                    getCountryWithName (event.getAttackingCountryName ()),
                                    getCountryWithName (event.getDefendingCountryName ()));
        }
      }
    });
  }

  @Handler
  final void onEvent (final BattleResultEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (!isSelf (event.getPerson ()))
    {
      log.debug ("Ignoring event because does not pertain to player [{}]. Event: [{}]", getSelfPlayer (), event);
      return;
    }

    lastResultEvent = event;

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        battleDialog.showBattleResult (event.getBattleResult ());
      }
    });
  }

  // @formatter:off
  // TODO Uncomment after PERIL-816 is completed by Brian Groenke.
  // @Handler
  // final void onEvent (final PlayerOrderRetreatDeniedEvent event)
  // {
  //   Arguments.checkIsNotNull (event, "event");
  //
  //   log.debug ("Event received [{}].", event);
  //   log.error ("Could not retreat. Reason: {}", event.getReason ());
  // }
  // @formatter:on

  private boolean isContinuingBattle (final BattleSetupEvent event)
  {
    return battleDialog.isShown () && lastResultEvent != null
            && lastResultEvent.battleOutcomeIs (BattleOutcome.CONTINUE)
            && lastResultEvent.getAttackingCountry ().equals (event.getAttackingCountry ())
            && lastResultEvent.getDefendingCountry ().equals (event.getDefendingCountry ())
            && lastResultEvent.getAttackingPlayer ().equals (event.getAttackingPlayer ())
            && lastResultEvent.getDefendingPlayer ().equals (event.getDefendingPlayer ());
  }
}
