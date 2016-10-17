/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerResponseTimeoutEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.state.events.BattleResultContinueEvent;
import com.forerunnergames.peril.core.model.state.events.BattleResultDefeatEvent;
import com.forerunnergames.peril.core.model.state.events.BattleResultVictoryEvent;
import com.forerunnergames.peril.core.model.state.events.BeginManualCountryAssignmentEvent;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.state.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;

import com.stateforge.statemachine.context.IContextEnd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

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

  public StateMachineEventHandler (final GameModel gameModel)
  {
    Arguments.checkIsNotNull (gameModel, "gameModel");

    context = new GameStateMachineContext (gameModel);
    context.setObserver (stateMachineListener);

    context.setEndHandler (new IContextEnd ()
    {
      @Override
      public void end (@Nullable final Throwable throwable)
      {
        errorState = Optional.fromNullable (throwable);

        stateMachineListener.end (throwable);
      }
    });
  }

  public String getCurrentGameStateName ()
  {
    final GameStateMachineOperatingParallel operatingState = context.getGameStateMachineOperatingParallel ();
    if (operatingState == null) throw new IllegalStateException ("State machine is not in operating state.");
    final GameStateMachineGameHandlerState current = operatingState.getGameStateMachineGameHandlerContext ()
            .getStateCurrent ();
    if (current == null) throw new IllegalStateException ("State machine is not in main game state.");
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
  public void onEvent (final CreateGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onCreateGameEvent (event);
  }

  @Handler
  public void onEvent (final DestroyGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onDestroyGameEvent (event);
  }

  @Handler
  public void onEvent (final EndGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onEndGameEvent (event);
  }

  @Handler
  public void onEvent (final DeterminePlayerTurnOrderCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onDeterminePlayerTurnOrderCompleteEvent (event);
  }

  @Handler
  public void onEvent (final DistributeInitialArmiesCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onDistributeInitialArmiesCompleteEvent (event);
  }

  @Handler
  public void onEvent (final BeginManualCountryAssignmentEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onBeginManualCountryAssignmentEvent (event);
  }

  @Handler
  public void onEvent (final RandomlyAssignPlayerCountriesEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onRandomlyAssignPlayerCountriesEvent (event);
  }

  @Handler
  public void onEvent (final PlayerClaimCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerClaimCountryResponseRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerClaimCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerClaimCountryResponseSuccessEvent (event);
  }

  @Handler
  public void onEvent (final PlayerCountryAssignmentCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerCountryAssignmentCompleteEvent (event);
  }

  @Handler
  public void onEvent (final EndInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onEndInitialReinforcementPhaseEvent (event);
  }

  @Handler
  public void onEvent (final PlayerReinforceCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerReinforceCountryRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerTradeInCardsRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerTradeInCardsRequestEvent (event);
  }

  @Handler
  public void onEvent (final EndReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onEndReinforcementPhaseEvent (event);
  }

  @Handler
  public void onEvent (final PlayerEndAttackPhaseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerEndAttackPhaseRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerDefendCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerDefendCountryResponseRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerSelectAttackVectorRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerSelectAttackVectorRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerOrderAttackRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerOrderAttackRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerOrderRetreatRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerOrderRetreatRequestEvent (event);
  }

  @Handler
  public void onEvent (final BattleResultVictoryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onBattleResultVictoryEvent (event);
  }

  @Handler
  public void onEvent (final BattleResultDefeatEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onBattleResultDefeatEvent (event);
  }

  @Handler
  public void onEvent (final BattleResultContinueEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onBattleResultContinueEvent (event);
  }

  @Handler
  public void onEvent (final PlayerOccupyCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerOccupyCountryResponseRequestEvent (event);
  }

  @Handler
  public void onEvent (final SkipFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onSkipFortifyPhaseEvent (event);
  }

  @Handler
  public void onEvent (final PlayerSelectFortifyVectorRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerSelectFortifyVectorRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerOrderFortifyRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerOrderFortifyRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerCancelFortifyRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerCancelFortifyRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerJoinGameRequestEvent (event);
  }

  @Handler
  public void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerJoinGameSuccessEvent (event);
  }

  @Handler
  public void onEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerJoinGameDeniedEvent (event);
  }

  @Handler
  public void onEvent (final EndPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onEndPlayerTurnEvent (event);
  }

  @Handler
  public void onEvent (final EndPlayerTurnRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onEndPlayerTurnRequestEvent (event);
  }

  @Handler
  public void onEvent (final SkipPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onSkipPlayerTurnEvent (event);
  }

  @Handler
  public void onEvent (final PlayerResponseTimeoutEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerResponseTimeoutEvent (event);
  }

  @Handler
  public void onEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Received event {}", event);

    context.onPlayerLeaveGameEvent (event);
  }

  private class CompositeStateMachineListener implements StateMachineListener
  {
    private final List <StateMachineListener> stateMachineListeners = new CopyOnWriteArrayList <> ();

    CompositeStateMachineListener ()
    {
      stateMachineListeners.add (new LoggingStateMachineListener ());
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
    public void onActionException (final String context, @Nullable final Throwable throwable)
    {
      Arguments.checkIsNotNull (context, "context");

      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.onActionException (context, throwable);
      }
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
    public void end (@Nullable final Throwable throwable)
    {
      for (final StateMachineListener listener : stateMachineListeners)
      {
        listener.end (throwable);
      }
    }
  }
}
