package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private final MBassador <Event> eventBus;
  private final PlayerModel playerModel;

  public GameModel (final PlayerModel playerModel, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.eventBus = eventBus;
  }

  // In use by GameStateMachine.fsmjava
  public void waitForPlayerJoinGameRequest()
  {
    log.info ("Waiting for player join game request...");
  }

  // In use by GameStateMachine.fsmjava
  public void handlePlayerJoinGameRequest()
  {
    log.info ("Handling player join game request...");

    // TODO Need to pass the actual player here through a PlayerJoinGameRequestEvent parameter,
    // TODO but have not figured out yet how to pass parameters into actions in GameStateMachine.fsmjava
    final Player player = PlayerFactory.createRandom();

    final Result result = playerModel.add (player);

    if (result.isSuccessful())
    {
      playerJoinGameSuccess (player);
    }
    else
    {
      playerJoinGameDenied (player, result.getMessage());
    }
  }

  private void playerJoinGameDenied (final Player player, final String reason)
  {
    log.info ("Denied adding player [{}]. Reason: {}.", player, reason);

    eventBus.publish (new PlayerJoinGameDeniedEvent (player.getName(), reason));
  }

  private void playerJoinGameSuccess (final Player player)
  {
    log.info ("Added player [{}]. New player count: {}.", player, playerModel.count());

    eventBus.publish (new PlayerJoinGameSuccessEvent (player));
  }

  // In use by GameStateMachine.fsmjava
  public void determinePlayerTurnOrder()
  {
    log.info ("Determining player turn order...");

    eventBus.publish (new DeterminePlayerTurnOrderCompleteEvent());
  }

  // In use by GameStateMachine.fsmjava
  public boolean isGameFull()
  {
    return playerModel.isFull();
  }
}
