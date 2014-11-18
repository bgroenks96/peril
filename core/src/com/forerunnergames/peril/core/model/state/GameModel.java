package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.tools.common.Arguments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  public static final int MAX_PLAYER_COUNT = 10;
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private GameStateEventListener listener = new NullGameStateEventListener();
  private int playerCount = 0;

  public void setGameStateEventListener (final GameStateEventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    this.listener = listener;
  }

  // In use by GameStateMachine.fsmjava
  public void waitForPlayerJoinGameRequest()
  {
    log.info ("Waiting for player join game request...");
  }

  // In use by GameStateMachine.fsmjava
  public void handlePlayerJoinGameRequest()
  {
    log.info ("Handling player join game request.");

    if (playerCount < MAX_PLAYER_COUNT)
    {
      listener.onPlayerJoinGameSuccessEvent();

      ++playerCount;
    }
    else
    {
      listener.onPlayerJoinGameDeniedEvent();
    }
  }

  // In use by GameStateMachine.fsmjava
  public void determinePlayerTurnOrder()
  {
    log.info ("Determining player turn order.");

    listener.onDeterminePlayerTurnOrderComplete();
  }

  // In use by GameStateMachine.fsmjava
  public boolean isGameFull()
  {
    return playerCount == MAX_PLAYER_COUNT;
  }

  private static class NullGameStateEventListener implements GameStateEventListener
  {
    @Override
    public void onCreateNewGameEvent()
    {
    }

    @Override
    public void onPlayerJoinGameRequestEvent()
    {
    }

    @Override
    public void onPlayerJoinGameSuccessEvent()
    {
    }

    @Override
    public void onPlayerJoinGameDeniedEvent()
    {
    }

    @Override
    public void onDeterminePlayerTurnOrderComplete()
    {
    }
  }
}
