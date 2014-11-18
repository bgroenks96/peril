package com.forerunnergames.peril.core.model.state;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.BeforeClass;
import org.junit.Test;

public class GameStateMachineTest
{
  private static final CountDownLatch countDownLatch = new CountDownLatch (1);
  private static GameStateMachine gameStateMachine;

  @BeforeClass
  public static void setUpClass()
  {
    final GameModel model = new GameModel();

    gameStateMachine = new GameStateMachine (model, new GameStateMachineListener()
    {
      @Override
      public void onEnd()
      {
        countDownLatch.countDown();
      }
    });

    model.setGameStateEventListener (gameStateMachine);
  }

  @Test
  public void testAll()
  {
    gameStateMachine.onCreateNewGameEvent();

    // Simulate filling up the game with players
    for (int i = 0; i < GameModel.MAX_PLAYER_COUNT; ++i)
    {
      gameStateMachine.onPlayerJoinGameRequestEvent();
    }

    try
    {
      countDownLatch.await();
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();

      assertTrue (false);
    }
  }
}
