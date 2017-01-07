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
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerAttackCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerEndTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerFortifyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerCancelFortifyDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerEndTurnDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerAttackCountryEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerFortifyCountryEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerReinforceCountryEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerSelectAttackVectorEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerSelectFortifyVectorEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialCountryAssignmentPhaseEvent;
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
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.GameResumedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.GameSuspendedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerDisconnectEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerAttackCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerClaimCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerDefendCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerFortifyCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerOccupyCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerReinforceCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerSelectAttackVectorWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerSelectFortifyVectorWaitEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerAttackCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerCancelFortifySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerDefendCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndTurnSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerRetreatSuccessEvent;
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
  void onEvent (final BeginInitialCountryAssignmentPhaseEvent event)
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
  void onEvent (final PlayerReinforceCountryEvent event)
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
  void onEvent (final PlayerReinforceCountryWaitEvent event)
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
  void onEvent (final PlayerSelectAttackVectorEvent event)
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
  void onEvent (final PlayerSelectAttackVectorWaitEvent event)
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
  void onEvent (final PlayerAttackCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    // NEVER retreating is bad for your health... ;-)
    if (shouldAct (RETREAT_PROBABILITY))
    {
      send (new PlayerRetreatRequestEvent ());
      return;
    }

    final DieRange dieRange = event.getAttackerDieRange ();

    send (new PlayerAttackCountryRequestEvent (chooseRandomly (dieRange.min (), dieRange.max ())));
  }

  @Handler
  void onEvent (final PlayerAttackCountryWaitEvent event)
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
  void onEvent (final PlayerAttackCountrySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerRetreatSuccessEvent event)
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

    send (new PlayerOccupyCountryResponseRequestEvent (
            chooseRandomly (event.getMinOccupationArmyCount (), event.getMaxOccupationArmyCount ())));
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
  void onEvent (final PlayerSelectFortifyVectorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);

    if (!isSelf (event)) return;

    final ImmutableMultimap <CountryPacket, CountryPacket> fortifyVectors = event.getValidVectors ();

    // Don't ALWAYS fortify... ;-)
    if (!shouldAct (FORTIFY_PROBABILITY))
    {
      send (new PlayerEndTurnRequestEvent ());
      return;
    }

    final CountryPacket source = chooseRandomly (fortifyVectors.keys ());
    final ImmutableCollection <CountryPacket> targets = fortifyVectors.get (source);
    final CountryPacket target = chooseRandomly (targets);

    send (new PlayerSelectFortifyVectorRequestEvent (source.getName (), target.getName ()));
  }

  @Handler
  void onEvent (final PlayerSelectFortifyVectorWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerFortifyCountryEvent event)
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

    send (new PlayerFortifyCountryRequestEvent (
            chooseRandomly (event.getMinDeltaArmyCount (), event.getMaxDeltaArmyCount ())));
  }

  @Handler
  void onEvent (final PlayerFortifyCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerFortifyCountrySuccessEvent event)
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
  void onEvent (final PlayerEndTurnSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerEndTurnDeniedEvent event)
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
  void onEvent (final EndGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", getPlayerName (), event);
  }

  @Handler
  void onEvent (final PlayerDisconnectEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
  }

  @Handler
  void onEvent (final GameSuspendedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
  }

  @Handler
  void onEvent (final GameResumedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
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
