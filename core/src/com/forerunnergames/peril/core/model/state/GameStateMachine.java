package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.tools.common.Arguments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stateforge.statemachine.context.IContextEnd;
import com.stateforge.statemachine.listener.ObserverConsole;

public final class GameStateMachine implements GameStateEventListener
{
  private static final Logger log = LoggerFactory.getLogger (GameStateMachine.class);
  private final GameStateMachineContext context;

  public GameStateMachine (final GameModel model, final GameStateMachineListener listener)
  {
    Arguments.checkIsNotNull (model, "model");
    Arguments.checkIsNotNull (listener, "listener");

    context = new GameStateMachineContext (model);
    context.setObserver (ObserverConsole.getInstance());

    context.setEndHandler (new IContextEnd()
    {
      @Override
      public void end (final Throwable throwable)
      {
        if (throwable != null)
        {
          log.error ("The state machine ended with an error.", throwable);
        }

        listener.onEnd();
      }
    });
  }

  @Override
  public void onCreateNewGameEvent()
  {
    context.onCreateNewGameEvent();
  }

  @Override
  public void onPlayerJoinGameRequestEvent()
  {
    context.onPlayerJoinGameRequestEvent();
  }

  @Override
  public void onPlayerJoinGameSuccessEvent()
  {
    context.onPlayerJoinGameSuccessEvent();
  }

  @Override
  public void onPlayerJoinGameDeniedEvent()
  {
    context.onPlayerJoinGameDeniedEvent();
  }

  @Override
  public void onDeterminePlayerTurnOrderComplete()
  {
    context.onDeterminePlayerTurnOrderComplete();
  }
}
