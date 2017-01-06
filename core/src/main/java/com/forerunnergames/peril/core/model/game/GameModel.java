/*
 * Copyright �� 2011 - 2013 Aaron Mahan.
 * Copyright �� 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.game;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerEndTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerEndTurnDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.WaitingForPlayersToJoinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerRestoreGameStateEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.events.internal.player.NotifyPlayerInputTimeoutEvent;
import com.forerunnergames.peril.core.events.internal.player.SendGameStateRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.SendGameStateResponseEvent;
import com.forerunnergames.peril.core.events.internal.player.SendGameStateResponseEvent.ResponseCode;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandler;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel.PlayerJoinGameStatus;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.listener.Handler;

public final class GameModel extends AbstractGamePhaseHandler
{
  private GamePhase resumeGamePhase = GamePhase.UNKNOWN;

  GameModel (final GameModelConfiguration gameModelConfig)
  {
    super (gameModelConfig);
  }

  @Override
  @StateExitAction
  public void resetTurn ()
  {
    playerTurnModel.resetCurrentTurn ();
  }

  @Override
  protected void onBegin ()
  {
    beginGame ();
  }

  @Override
  protected void onEnd ()
  {
    endGame ();
  }

  public static GameModel create (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    return new GameModel (GameModelConfiguration.builder (rules).build ());
  }

  public static GameModel create (final GameModelConfiguration gameModelConfig)
  {
    Arguments.checkIsNotNull (gameModelConfig, "gameModelConfig");

    return new GameModel (gameModelConfig);
  }

  @StateEntryAction
  public void waitForGameToBegin ()
  {
    log.info ("Waiting for game to begin...");

    publish (new WaitingForPlayersToJoinGameEvent ());
  }

  @StateEntryAction
  public void suspendGame ()
  {
    log.info ("Suspending game...");

    resumeGamePhase = getCurrentGamePhase ();
    changeGamePhaseTo (GamePhase.SUSPENDED);
  }

  @StateTransitionAction
  public void resumeGame ()
  {
    log.info ("Resuming game...");

    changeGamePhaseTo (resumeGamePhase);
  }

  @StateTransitionCondition
  public boolean skipPlayerTurn (final SkipPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    if (getCurrentPlayerPacket ().isNot (event.getPerson ()))
    {
      return false;
    }

    log.info ("Skipping turn for player [{}].", event.getPersonName ());

    return true;
  }

  @StateTransitionAction
  public void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerFactory playerFactory = new PlayerFactory ();
    playerFactory.newPlayerWith (event.getPlayerName (), event.getPlayerSentience ());
    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (playerFactory);

    // for loop is a formality; there should only ever be one result for this case.
    for (final PlayerJoinGameStatus result : results)
    {
      final PlayerPacket player = result.getPlayer ();
      if (result.failed ())
      {
        publish (new PlayerJoinGameDeniedEvent (player.getName (), event, result.getFailureReason ()));
        continue;
      }

      publish (new PlayerJoinGameSuccessEvent (player, playerModel.getPlayerPackets (), rules.getPersonLimits ()));
    }
  }

  @StateTransitionCondition
  public boolean isFull ()
  {
    return playerModel.isFull ();
  }

  @StateTransitionCondition
  public boolean isNotFull ()
  {
    return playerModel.isNotFull ();
  }

  public boolean isEmpty ()
  {
    return playerModel.isEmpty ();
  }

  public boolean playerCountIs (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return playerModel.playerCountIs (count);
  }

  public boolean playerLimitIs (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIs (limit);
  }

  public int getPlayerCount ()
  {
    return playerModel.getPlayerCount ();
  }

  public int getPlayerLimit ()
  {
    return playerModel.getPlayerLimit ();
  }

  public boolean isFirstTurn ()
  {
    return playerTurnModel.isFirstTurn ();
  }

  public boolean isLastTurn ()
  {
    return playerTurnModel.isLastTurn ();
  }

  public GameModelConfiguration getConfiguration ()
  {
    return gameModelConfig;
  }

  public void dumpDataCacheToLog ()
  {
    log.debug ("CurrentTurn: {} | Player: [{}] | Cache dump: [{}]", playerTurnModel.getCurrentTurn (),
               getCurrentPlayerId (), turnDataCache);
  }

  // ------- Game Phase Event Handlers ------- //

  @Handler (priority = Integer.MIN_VALUE)
  void onEvent (final PlayerEndTurnRequestEvent event)
  {
    if (turnDataCache.isSet (CacheKey.END_PLAYER_TURN_VERIFIED)) return;

    final Optional <PlayerPacket> sender = eventRegistry.senderOf (event);
    if (!sender.isPresent () || sender.get ().isNot (getCurrentPlayerPacket ()))
    {
      publish (new PlayerEndTurnDeniedEvent (sender.get (), event, PlayerEndTurnDeniedEvent.Reason.NOT_IN_TURN));
      return;
    }

    publish (new PlayerEndTurnDeniedEvent (getCurrentPlayerPacket (), event,
            PlayerEndTurnDeniedEvent.Reason.ACTION_REQUIRED));
  }

  @Handler
  void onEvent (final NotifyPlayerInputTimeoutEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Internal event received [{}]", event);

    // handler will verify whether or not the skip player turn event is valid
    publish (new SkipPlayerTurnEvent (event.getPlayer (), SkipPlayerTurnEvent.Reason.PLAYER_INPUT_TIMED_OUT));
  }

  @Handler
  void onEvent (final SendGameStateRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Internal event received [{}]", event);

    final String targetPlayerName = event.getTargetPlayer ().getName ();
    if (!playerModel.existsPlayerWith (targetPlayerName))
    {
      publish (new SendGameStateResponseEvent (ResponseCode.PLAYER_NOT_FOUND, event.getEventId ()));
      return;
    }

    final Id targetPlayerId = playerModel.idOf (targetPlayerName);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();
    final GamePhase currentPhase = getCurrentGamePhase ();
    final int currentRoundNumber = playerTurnModel.getRound ();
    final ImmutableMap <CountryPacket, PlayerPacket> countriesToPlayers = buildPlayMapViewFrom (playerModel,
                                                                                                playMapModel);
    final CardSetPacket cardsInHand = CardPackets.fromCards (cardModel.getCardsInHand (targetPlayerId));
    final ImmutableSet <CardSetPacket> availableTradeIns = CardPackets
            .fromCardMatchSet (cardModel.computeMatchesFor (targetPlayerId));
    publish (new PlayerRestoreGameStateEvent (event.getTargetPlayer (), currentPlayer, currentPhase, currentRoundNumber,
            cardsInHand, availableTradeIns, countriesToPlayers, event.getGameServerConfiguration ()));
    publish (new SendGameStateResponseEvent (ResponseCode.OK, event.getEventId ()));
  }

  private void beginGame ()
  {
    log.info ("Starting a new game...");

    playerModel.removeAllArmiesFromHandsOfAllPlayers ();
    playerModel.removeAllCardsFromHandsOfAllPlayers ();
    countryOwnerModel.unassignAllCountries ();
    countryArmyModel.resetAllCountries ();
    playerTurnModel.resetAll ();

    publish (new BeginGameEvent ());
  }

  private void endGame ()
  {
    log.info ("Game over.");

    changeGamePhaseTo (GamePhase.END);

    // TODO End the game gracefully - this can be called DURING ANY GAME STATE
  }
}
