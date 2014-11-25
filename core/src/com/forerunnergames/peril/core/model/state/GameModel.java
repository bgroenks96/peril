package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  public static final int MAX_PLAYER_COUNT = 10;
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private final MBassador <Event> eventBus;
  private int playerCount = 0;

  public GameModel (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

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

    final Player player = createPlayer();

    if (isGameFull())
    {
      deny (player, "The game is full");
    }
    else
    {
      add (player);
    }
  }

  private Player createPlayer()
  {
    return PlayerFactory.createRandom();
  }

  private void deny (final Player player, final String reason)
  {
    log.info ("Denied adding {}. Reason: {}.", player, reason);

    eventBus.publish (new PlayerJoinGameDeniedEvent (player.getName(), reason));
  }

  private void add (final Player player)
  {
    ++playerCount;

    log.info ("Added {}. New player count: {}.", player, playerCount);

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
    return playerCount == MAX_PLAYER_COUNT;
  }
}
