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

import com.forerunnergames.peril.client.events.SelectAttackSourceCountryRequestEvent;
import com.forerunnergames.peril.client.events.SelectAttackTargetCountryRequestEvent;
import com.forerunnergames.peril.client.events.SelectCountryRequestEvent;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.BattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.net.events.client.interfaces.BattleRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOrderAttackDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectAttackVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginAttackEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectAttackVectorSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class AttackingBattlePhaseHandler extends AbstractBattlePhaseHandler
{
  private final CountryVectorSelectionHandler countryVectorSelectionHandler;

  public AttackingBattlePhaseHandler (final PlayMap playMap,
                                      final BattleDialog attackDialog,
                                      final BattleResultDialog resultDialog,
                                      final MBassador <Event> eventBus)
  {
    super (playMap, attackDialog, resultDialog, eventBus);

    countryVectorSelectionHandler = new AttackingBattlePhaseCountryVectorSelectionHandler (playMap, eventBus);
  }

  @Override
  public void reset ()
  {
    super.reset ();
    countryVectorSelectionHandler.reset ();
  }

  @Override
  protected BattleRequestEvent createBattleRequestEvent (final int dieCount)
  {
    return new PlayerOrderAttackRequestEvent (dieCount);
  }

  @Override
  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    super.setPlayMap (playMap);
    countryVectorSelectionHandler.setPlayMap (playMap);
  }

  @Handler
  void onEvent (final PlayerBeginAttackEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    reset ();
    countryVectorSelectionHandler.start (event);
  }

  @Handler
  void onEvent (final PlayerSelectAttackVectorSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
  }

  @Handler
  void onEvent (final PlayerSelectAttackVectorDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Could not attack. Reason: {}", event.getReason ());

    reset ();
    countryVectorSelectionHandler.restart ();
  }

  @Handler
  void onEvent (final PlayerOrderAttackDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Could not attack. Reason: {}", event.getReason ());

    reset ();
    countryVectorSelectionHandler.restart ();
  }

  private static class AttackingBattlePhaseCountryVectorSelectionHandler extends AbstractCountryVectorSelectionHandler
  {
    private final MBassador <Event> eventBus;

    public AttackingBattlePhaseCountryVectorSelectionHandler (final PlayMap playMap, final MBassador <Event> eventBus)
    {
      super (playMap, eventBus);

      this.eventBus = eventBus;
    }

    @Override
    SelectCountryRequestEvent createSourceCountrySelectionRequest ()
    {
      return new SelectAttackSourceCountryRequestEvent ();
    }

    @Override
    SelectCountryRequestEvent createTargetCountrySelectionRequest (final String sourceCountryName)
    {
      Arguments.checkIsNotNull (sourceCountryName, "sourceCountryName");

      return new SelectAttackTargetCountryRequestEvent (sourceCountryName);
    }

    @Override
    public void onEnd (final String sourceCountryName, final String targetCountryName)
    {
      eventBus.publish (new PlayerSelectAttackVectorRequestEvent (sourceCountryName, targetCountryName));
    }
  }
}
