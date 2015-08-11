package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.StateMachineActionHandler;
import com.forerunnergames.peril.core.model.state.events.BeginManualCountrySelectionEvent;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.state.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.state.events.EndGameEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;

import com.stateforge.statemachine.context.IContextEnd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * Then, when an external event is received, delegate to the corresponding state machine context method.
 *
 * "External" events can come from anywhere, including inside the state machine's action-handling classes.
 */
// @formatter:on
public final class StateMachineEventHandler
{
  private static final Logger log = LoggerFactory.getLogger (StateMachineEventHandler.class);
  private final GameStateMachineContext context;
  private final CompositeStateMachineListener stateMachineListener = new CompositeStateMachineListener ();
  private Optional <Throwable> errorState = Optional.absent ();

  public StateMachineEventHandler (final StateMachineActionHandler stateMachineActionHandler)
  {
    Arguments.checkIsNotNull (stateMachineActionHandler, "stateMachineActionHandler");

    context = new GameStateMachineContext (stateMachineActionHandler);
    context.setObserver (stateMachineListener);

    context.setEndHandler (new IContextEnd ()
    {
      @Override
      public void end (final Throwable throwable)
      {
        Arguments.checkIsNotNull (throwable, "throwable");

        errorState = Optional.fromNullable (throwable);

        stateMachineListener.end (throwable);
      }
    });
  }

  public String getCurrentGameStateName ()
  {
    final GameStateMachineOperatingParallel operatingState = context.getGameStateMachineOperatingParallel ();
    final GameStateMachineGameHandlerState current = operatingState.getGameStateMachineGameHandlerContext ()
            .getStateCurrent ();
    if (current == null) throw new IllegalStateException ("State machine is no longer in main game state.");
    return current.getName ();
  }

  public Optional <Throwable> checkError ()
  {
    return errorState;
  }

  public void addStateMachineListener (final StateMachineListener stateMachineListener)
  {
    Arguments.checkIsNotNull (stateMachineListener, "stateMachineListener");

    this.stateMachineListener.add (stateMachineListener);
  }

  public void removeStateListener (final StateMachineListener stateMachineListener)
  {
    Arguments.checkIsNotNull (stateMachineListener, "stateMachineListener");

    this.stateMachineListener.remove (stateMachineListener);
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
  public void onEndGameEvent (final EndGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onEndGameEvent (event);
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
  public void onBeginManualCountrySelectionEvent (final BeginManualCountrySelectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onBeginManualCountrySelectionEvent (event);
  }

  @Handler
  public void onRandomlyAssignPlayerCountriesEvent (final RandomlyAssignPlayerCountriesEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onRandomlyAssignPlayerCountriesEvent (event);
  }

  @Handler
  public void onPlayerSelectCountryResponseRequestEvent (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerSelectCountryResponseRequestEvent (event);
  }

  @Handler
  public void onPlayerSelectCountryResponseSuccessEvent (final PlayerSelectCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerSelectCountryResponseSuccessEvent (event);
  }

  @Handler
  public void onPlayerCountryAssignmentCompleteEvent (final PlayerCountryAssignmentCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerCountryAssignmentCompleteEvent (event);
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

  @Handler
  public void onPlayerLeaveGameEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Received event {}", event);

    context.onPlayerLeaveGameEvent (event);
  }

  private class CompositeStateMachineListener implements StateMachineListener
  {
    private final List <StateMachineListener> stateMachineListeners = new CopyOnWriteArrayList <> ();

    CompositeStateMachineListener ()
    {
      stateMachineListeners.add (new LoggingStateMachineListener ());
    }

    public void add (final StateMachineListener listener)
    {
      Arguments.checkIsNotNull (listener, "listener");

      stateMachineListeners.add (listener);
    }

    public void remove (final StateMachineListener listener)
    {
      Arguments.checkIsNotNull (listener, "listener");

      stateMachineListeners.remove (listener);
    }

    @Override
    public void onEntry (final String context, final String state)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (state, "state");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onEntry (context, state);
      }
    }

    @Override
    public void onExit (final String context, final String state)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (state, "state");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onExit (context, state);
      }
    }

    @Override
    public void onTransitionBegin (final String context,
                                   final String statePrevious,
                                   final String stateNext,
                                   final String transition)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (statePrevious, "statePrevious");
      Arguments.checkIsNotNull (stateNext, "stateNext");
      Arguments.checkIsNotNull (transition, "transition");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onTransitionBegin (context, statePrevious, stateNext, transition);
      }
    }

    @Override
    public void onTransitionEnd (final String context,
                                 final String statePrevious,
                                 final String stateNext,
                                 final String transition)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (statePrevious, "statePrevious");
      Arguments.checkIsNotNull (stateNext, "stateNext");
      Arguments.checkIsNotNull (transition, "transition");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onTransitionBegin (context, statePrevious, stateNext, transition);
      }
    }

    @Override
    public void onTimerStart (final String context, final String name, final long duration)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (name, "name");
      Arguments.checkIsNotNegative (duration, "duration");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onTimerStart (context, name, duration);
      }
    }

    @Override
    public void onTimerStop (final String context, final String name)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (name, "name");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onTimerStop (context, name);
      }
    }

    @Override
    public void onActionException (final String context, final Throwable throwable)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (throwable, "throwable");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onActionException (context, throwable);
      }
    }

    @Override
    public void end (final Throwable throwable)
    {
      Arguments.checkIsNotNull (throwable, "throwable");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.end (throwable);
      }
    }
  }
}