package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.state.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.state.events.EndGameEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;

import com.stateforge.statemachine.context.IContextEnd;
import com.stateforge.statemachine.listener.ObserverConsole;

import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @formatter:off
/**
 * This is the outermost layer of the core game logic state machine.
 *
 * It's sole purpose is to feed external events into the internal game state machine context.
 *
 * This is accomplished in the following manner:
 *
 * First, subscribe to (listen for) relevant external events on the event bus.
 * Then, when an external even is received, delegate to the corresponding state machine context method.
 *
 * "External" events can come from anywhere, including inside the state machine's action-handling classes.
 */
// @formatter:on
public final class GameStateMachine
{
  private static final Logger log = LoggerFactory.getLogger (GameStateMachine.class);
  private final GameStateMachineContext context;

  public GameStateMachine (final GameModel gameModel)
  {
    this (gameModel, new GameStateMachineListener ()
    {
      @Override
      public void onEnd ()
      {
      }
    });
  }

  public GameStateMachine (final GameModel gameModel, final GameStateMachineListener listener)
  {
    Arguments.checkIsNotNull (gameModel, "gameModel");
    Arguments.checkIsNotNull (listener, "listener");

    context = new GameStateMachineContext (gameModel);
    context.setObserver (ObserverConsole.getInstance ());

    context.setEndHandler (new IContextEnd ()
    {
      @Override
      public void end (final Throwable throwable)
      {
        if (throwable != null)
        {
          log.error ("The state machine ended with an error.", throwable);
        }

        listener.onEnd ();
      }
    });
  }

  @Handler
  public void onChangePlayerColorRequestEvent (final ChangePlayerColorRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onChangePlayerColorRequestEvent (event);
  }

  @Handler
  public void onCreateGameEvent (final CreateGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onCreateGameEvent (event);
  }

  @Handler
  public void onDestroyGameEvent (final DestroyGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onDestroyGameEvent (event);
  }

  @Handler
  public void onDeterminePlayerTurnOrderCompleteEvent (final DeterminePlayerTurnOrderCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onDeterminePlayerTurnOrderCompleteEvent (event);
  }

  @Handler
  public void onDistributeInitialArmiesCompleteEvent (final DistributeInitialArmiesCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onDistributeInitialArmiesCompleteEvent (event);
  }

  @Handler
  public void onRandomlyAssignPlayerCountriesEvent (final RandomlyAssignPlayerCountriesEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onRandomlyAssignPlayerCountriesEvent (event);
  }

  @Handler
  public void onPlayerCountryAssignmentCompleteEvent (final PlayerCountryAssignmentCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerCountryAssignmentCompleteEvent (event);
  }

  @Handler
  public void onEndGameEvent (final EndGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onEndGameEvent (event);
  }

  @Handler
  public void onPlayerJoinGameDeniedEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerJoinGameDeniedEvent (event);
  }

  @Handler
  public void onPlayerJoinGameRequestEvent (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerJoinGameRequestEvent (event);
  }

  @Handler
  public void onPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerJoinGameSuccessEvent (event);
  }
}
