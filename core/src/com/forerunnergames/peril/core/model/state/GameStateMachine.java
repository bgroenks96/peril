package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateNewGameEvent;
import com.forerunnergames.peril.core.model.events.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;

import com.stateforge.statemachine.context.IContextEnd;
import com.stateforge.statemachine.listener.ObserverConsole;

import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the outermost layer of the core game logic state machine.
 *
 * It's sole purpose is to feed external events into the internal game state machine context.
 *
 * This is accomplished in the following manner:
 *
 * First, subscribe to (listen for) relevant external events on the event bus.
 * Then, when an external even is received, delegate to the corresponding state machine context method.
 */
public final class GameStateMachine
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

  @Handler
  public void onEvent (final CreateNewGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onCreateNewGameEvent();
  }

  @Handler
  public void onEvent (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerJoinGameRequestEvent();
  }

  @Handler
  public void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerJoinGameSuccessEvent();
  }

  @Handler
  public void onEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerJoinGameDeniedEvent();
  }

  @Handler
  public void onEvent (final DeterminePlayerTurnOrderCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onDeterminePlayerTurnOrderComplete();
  }
}
