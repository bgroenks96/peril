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

package com.forerunnergames.peril.ai.processors;

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.EndPlayerTurnDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerCancelFortifyDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginAttackWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginFortificationWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginReinforcementWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerClaimCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerDefendCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerIssueAttackOrderWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerIssueFortifyOrderWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerOccupyCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginAttackEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginFortificationEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueAttackOrderEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueFortifyOrderEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.EndPlayerTurnSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerCancelFortifySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerDefendCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderAttackSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderFortifySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderRetreatSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectAttackVectorSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class GameLogicProcessor extends AbstractAiProcessor
{
  private static final double ATTACK_PROBABILITY = 0.8;
  private static final double RETREAT_PROBABILITY = 0.1;
  private static final double FORTIFY_PROBABILITY = 0.5;
  private static final double CANCEL_FORTIFY_PROBABILITY = 0.1;

  public GameLogicProcessor (final String playerName,
                             final GameServerConfiguration gameServerConfig,
                             final MBassador <Event> eventBus)
  {
    super (playerName, gameServerConfig, eventBus);
  }

  @Handler
  void onEvent (final ChatMessageSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final DeterminePlayerTurnOrderCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginPlayerCountryAssignmentEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerClaimCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    if (!isSelf (event)) return;

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    send (new PlayerClaimCountryResponseRequestEvent (chooseRandomly (event.getUnclaimedCountryNames ())));
  }

  @Handler
  void onEvent (final PlayerClaimCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerBeginReinforcementEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    final String country = chooseRandomly (event.getReinforceableCountryNames ());
    final boolean noChoice = event.getReinforceableCountryCount () == 1;
    final int minArmies = event.getMinReinforcementsPlacedPerCountry ();
    final int maxArmies = event.getMaxReinforcementsToPlaceOn (country);
    final int reinforcements = noChoice ? maxArmies : chooseRandomly (minArmies, maxArmies);

    send (new PlayerReinforceCountryRequestEvent (country, reinforcements));
  }

  @Handler
  void onEvent (final PlayerBeginReinforcementWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerReinforceCountrySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerReinforceCountryDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginRoundEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerCardTradeInAvailableEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;
    if (event.isTradeInRequired ()) send (new PlayerTradeInCardsRequestEvent (chooseRandomly (event.getMatches ())));
  }

  @Handler
  void onEvent (final PlayerTradeInCardsResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerBeginAttackEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    final ImmutableMultimap <CountryPacket, CountryPacket> attackVectors = event.getValidVectors ();

    // TODO Remove when PERIL-824 ("End Attack Phase If No Valid Attack Targets") is completed.
    if (attackVectors.isEmpty ())
    {
      send (new PlayerEndAttackPhaseRequestEvent ());
      return;
    }

    // Don't ALWAYS attack... ;-)
    if (!shouldAct (ATTACK_PROBABILITY))
    {
      send (new PlayerEndAttackPhaseRequestEvent ());
      return;
    }

    final CountryPacket source = chooseRandomly (attackVectors.keys ());
    final ImmutableCollection <CountryPacket> targets = attackVectors.get (source);
    final CountryPacket target = chooseRandomly (targets);

    send (new PlayerSelectAttackVectorRequestEvent (source.getName (), target.getName ()));
  }

  @Handler
  void onEvent (final PlayerBeginAttackWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerSelectAttackVectorSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerIssueAttackOrderEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    // NEVER retreating is bad for your health... ;-)
    if (shouldAct (RETREAT_PROBABILITY))
    {
      send (new PlayerOrderRetreatRequestEvent ());
      return;
    }

    final DieRange dieRange = event.getAttackerDieRange ();

    send (new PlayerOrderAttackRequestEvent (chooseRandomly (dieRange.min (), dieRange.max ())));
  }

  @Handler
  void onEvent (final PlayerIssueAttackOrderWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerDefendCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    final DieRange dieRange = event.getDefenderDieRange ();

    send (new PlayerDefendCountryResponseRequestEvent (dieRange.max ()));
  }

  @Handler
  void onEvent (final PlayerDefendCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerOrderAttackSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerOrderRetreatSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerDefendCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerDefendCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerEndAttackPhaseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerLoseGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerWinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerOccupyCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    send (new PlayerOccupyCountryResponseRequestEvent (chooseRandomly (event.getMinOccupationArmyCount (),
                                                                       event.getMaxOccupationArmyCount ())));
  }

  @Handler
  void onEvent (final PlayerOccupyCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final SkipFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final BeginFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerBeginFortificationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    final ImmutableMultimap <CountryPacket, CountryPacket> fortifyVectors = event.getValidVectors ();

    // Don't ALWAYS fortify... ;-)
    if (!shouldAct (FORTIFY_PROBABILITY))
    {
      send (new EndPlayerTurnRequestEvent ());
      return;
    }

    final CountryPacket source = chooseRandomly (fortifyVectors.keys ());
    final ImmutableCollection <CountryPacket> targets = fortifyVectors.get (source);
    final CountryPacket target = chooseRandomly (targets);

    send (new PlayerSelectFortifyVectorRequestEvent (source.getName (), target.getName ()));
  }

  @Handler
  void onEvent (final PlayerBeginFortificationWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerIssueFortifyOrderEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    // NEVER cancelling a fortification could lead to strategic failure... ;-)
    if (shouldAct (CANCEL_FORTIFY_PROBABILITY))
    {
      send (new PlayerCancelFortifyRequestEvent ());
      return;
    }

    send (new PlayerOrderFortifyRequestEvent (chooseRandomly (event.getMinDeltaArmyCount (),
                                                              event.getMaxDeltaArmyCount ())));
  }

  @Handler
  void onEvent (final PlayerIssueFortifyOrderWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerOrderFortifySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerCancelFortifySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerCancelFortifyDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndPlayerTurnSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndPlayerTurnDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final SkipPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndRoundEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final EndGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final ActivePlayerChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler (priority = EVENT_HANDLER_PRIORITY_CALL_LAST)
  void onEvent (final CountryOwnerChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("{}] received event [{}].", getPlayerName (), event);
  }

  @Handler (priority = EVENT_HANDLER_PRIORITY_CALL_LAST)
  void onEvent (final PlayerArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("{}] received event [{}].", getPlayerName (), event);
  }

  @Handler (priority = EVENT_HANDLER_PRIORITY_CALL_LAST)
  void onEvent (final PlayerTurnOrderChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("{}] received event [{}].", getPlayerName (), event);
  }
}
