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

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerQuitGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerQuitGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.WaitingForPlayersToJoinGameEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerQuitGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandler;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel.PlayerJoinGameStatus;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel extends AbstractGamePhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);

  GameModel (final GameModelConfiguration gameModelConfig)
  {
    super (gameModelConfig);

    eventBus.subscribe (internalCommHandler);
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
  }

  @StateTransitionAction
  public void resumeGame ()
  {
    log.info ("Resuming game...");
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
        publish (new PlayerJoinGameDeniedEvent (player.getName (), result.getFailureReason ()));
        continue;
      }

      publish (new PlayerJoinGameSuccessEvent (player, playerModel.getPlayerPackets (), rules.getPersonLimits ()));
    }
  }

  @StateTransitionAction
  public void handlePlayerQuitGameRequest (final PlayerQuitGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    final Optional <PlayerPacket> senderMaybe = internalCommHandler.senderOf (event);
    if (!senderMaybe.isPresent ())
    {
      publish (new PlayerQuitGameDeniedEvent (PlayerQuitGameDeniedEvent.Reason.PLAYER_DOES_NOT_EXIST));
      return;
    }

    final PlayerPacket sender = senderMaybe.get ();
    // removePlayerFromGame (playerModel.idOf (sender.getName ()));
    publish (new PlayerQuitGameSuccessEvent (sender));
  }

  @Override
  @StateExitAction
  public void resetTurn ()
  {
    playerTurnModel.resetCurrentTurn ();
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

  private void beginGame ()
  {
    log.info ("Starting a new game...");

    playerModel.removeAllArmiesFromHandsOfAllPlayers ();
    playerModel.removeAllCardsFromHandsOfAllPlayers ();
    countryOwnerModel.unassignAllCountries ();
    countryArmyModel.resetAllCountries ();
    playerTurnModel.resetCurrentTurn ();
    playerTurnModel.resetTurnCount ();

    publish (new BeginGameEvent ());

    // TODO Reset entire game state.
  }

  private void endGame ()
  {
    log.info ("Game over.");

    // TODO End the game gracefully - this can be called DURING ANY GAME STATE
  }
}
