package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.state.StateEntryAction;
import com.forerunnergames.peril.core.model.state.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineCondition;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.core.shared.events.player.InternalPlayerLeaveGameEvent;
import com.forerunnergames.peril.core.shared.events.player.UpdatePlayerDataRequestEvent;
import com.forerunnergames.peril.core.shared.events.player.UpdatePlayerDataResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StateMachineActionHandler
{
  private static final Logger log = LoggerFactory.getLogger (StateMachineActionHandler.class);
  private final PlayerModel playerModel;
  private final PlayMapModel playMapModel;
  private final PlayerTurnModel playerTurnModel;
  private final GameModel gameModel;
  private final GameRules rules;
  private final MBassador <Event> eventBus;

  public StateMachineActionHandler (final GameModel gameModel)
  {
    Arguments.checkIsNotNull (gameModel, "gameModel");

    this.gameModel = gameModel;
    playerModel = gameModel.getPlayerModel ();
    playMapModel = gameModel.getPlayMapModel ();
    playerTurnModel = gameModel.getPlayerTurnModel ();
    rules = gameModel.getRules ();
    eventBus = gameModel.getEventBus ();
    eventBus.subscribe (new InternalCommunicationHandler ());
  }

  @StateMachineAction
  @StateTransitionAction
  public void beginGame ()
  {
    log.info ("Starting a new game...");

    playerModel.removeAllArmiesFromHandsOfAllPlayers ();
    playMapModel.unassignAllCountries ();

    // TODO Clear all country armies.
    // TODO Reset entire game state.
  }

  @StateMachineAction
  @StateEntryAction
  public void endGame ()
  {
    log.info ("Game over.");

    // TODO End the game gracefully - this can be called DURING ANY GAME STATE
  }

  @StateMachineAction
  @StateEntryAction
  public void determinePlayerTurnOrder ()
  {
    gameModel.determinePlayerTurnOrder ();
  }

  @StateMachineAction
  @StateEntryAction
  public void distributeInitialArmies ()
  {
    gameModel.distributeInitialArmies ();
  }

  @StateMachineAction
  @StateEntryAction
  public void waitForCountrySelectionToBegin ()
  {
    gameModel.waitForCountrySelectionToBegin ();
  }

  @StateMachineAction
  @StateEntryAction
  public void randomlyAssignPlayerCountries ()
  {
    gameModel.randomlyAssignPlayerCountries ();
  }

  @StateMachineAction
  @StateEntryAction
  public void beginGameRound ()
  {
    gameModel.beginRound ();
  }

  @StateMachineAction
  @StateTransitionAction
  public void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    gameModel.handlePlayerJoinGameRequest (event);
  }

  /**
   * This method will be called after {@link InternalCommunicationHandler} has already handled the
   * {@link InternalPlayerLeaveGameEvent}.
   */
  @StateMachineAction
  @StateTransitionAction
  public void handlePlayerLeaveGame (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    gameModel.handlePlayerLeaveGame (event);
  }

  @StateMachineAction
  @StateEntryAction
  public void waitForPlayersToSelectInitialCountries ()
  {
    gameModel.waitForPlayersToSelectInitialCountries ();
  }

  @StateMachineCondition
  public boolean verifyPlayerCountrySelectionRequest (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return gameModel.verifyPlayerCountrySelectionRequest (event);
  }

  @StateMachineCondition
  public boolean isFull ()
  {
    return gameModel.isFull ();
  }

  @StateMachineCondition
  public boolean isNotFull ()
  {
    return gameModel.isNotFull ();
  }

  public boolean isEmpty ()
  {
    return gameModel.isEmpty ();
  }

  public boolean playerCountIs (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return gameModel.playerCountIs (count);
  }

  public boolean playerCountIsNot (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return gameModel.playerCountIsNot (count);
  }

  public boolean playerLimitIs (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return gameModel.playerLimitIs (limit);
  }

  public int getPlayerCount ()
  {
    return gameModel.getPlayerCount ();
  }

  public int getPlayerLimit ()
  {
    return gameModel.getPlayerLimit ();
  }

  public PlayerTurnOrder getTurn ()
  {
    return gameModel.getTurn ();
  }

  public boolean playerLimitIsAtLeast (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return gameModel.playerLimitIsAtLeast (limit);
  }

  // Handler class for internal communication events from server
  private class InternalCommunicationHandler
  {
    @Handler
    void onEvent (final UpdatePlayerDataRequestEvent event)
    {
      Arguments.checkIsNotNull (event, "event");

      final ImmutableSet <PlayerPacket> players = Packets.fromPlayers (playerModel.getPlayers ());
      eventBus.publish (new UpdatePlayerDataResponseEvent (players, event.getEventId ()));
    }

    @Handler
    void onEvent (final InternalPlayerLeaveGameEvent event)
    {
      Arguments.checkIsNotNull (event, "event");

      log.debug ("Event received [{}]", event);

      if (!playerModel.existsPlayerWith (event.getPlayerName ())) return;

      final Player player = playerModel.playerWith (event.getPlayerName ());

      playMapModel.unassignAllCountriesOwnedBy (player.getId ());
      playerModel.remove (player);
      playerTurnModel.setTurnCount (getPlayerLimit ());

      eventBus.publish (new PlayerLeaveGameEvent (event.getPlayer (), Packets.fromPlayers (playerModel.getPlayers ())));
    }
  }
}
