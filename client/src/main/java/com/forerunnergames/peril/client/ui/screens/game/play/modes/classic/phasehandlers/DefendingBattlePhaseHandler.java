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
import com.forerunnergames.peril.common.net.events.client.interfaces.BattleRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderRetreatSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class DefendingBattlePhaseHandler extends AbstractBattlePhaseHandler
{
  public DefendingBattlePhaseHandler (final PlayMap playMap,
                                      final BattleDialog defendDialog,
                                      final BattleResultDialog resultDialog,
                                      final MBassador <Event> eventBus)
  {
    super (playMap, defendDialog, resultDialog, eventBus);
  }

  @Override
  protected BattleRequestEvent createBattleRequestEvent (final int dieCount)
  {
    return new PlayerDefendCountryResponseRequestEvent (dieCount);
  }

  @Handler
  void onEvent (final PlayerOrderRetreatSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    reset ();
  }

  @Handler
  void onEvent (final PlayerDefendCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.error ("Could not defend. Reason: {}", event.getReason ());

    reset ();
  }
}
