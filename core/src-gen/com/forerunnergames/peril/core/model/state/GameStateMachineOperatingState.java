
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.events.EndGameEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;

public class GameStateMachineOperatingState
    extends GameStateMachineRootState
{

    private final static GameStateMachineOperatingState instance = new GameStateMachineOperatingState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineOperatingState() {
        setName("Operating");
        setStateParent(GameStateMachineRootState.getInstance());
    }

    /**
     * Get the State Instance
     * 
     */
    public static GameStateMachineRootState getInstance() {
        return instance;
    }

    /**
     * onEntry
     * 
     */
    @Override
    public void onEntry(GameStateMachineContext context) {
        context.getObserver().onEntry(context.getName(), this.getName());
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        context.setContextParallel(parallelOperating);
        parallelOperating.setActiveContext(2);
        parallelOperating.getGameStateMachinePlayerHandlerContext().enterInitialState();
        parallelOperating.getGameStateMachineGameHandlerContext().enterInitialState();
    }

    /**
     * onExit
     * 
     */
    @Override
    public void onExit(GameStateMachineContext context) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        context.setContextParallel(null);
        parallelOperating.getGameStateMachinePlayerHandlerContext().leaveCurrentState();
        parallelOperating.getGameStateMachineGameHandlerContext().leaveCurrentState();
        context.getObserver().onExit(context.getName(), this.getName());
    }

    /**
     * Event id: onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(GameStateMachineContext context, CreateGameEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onCreateGameEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onCreateGameEvent(event);
    }

    /**
     * Event id: onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(GameStateMachineContext context, DestroyGameEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onDestroyGameEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onDestroyGameEvent(event);
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Transition from Operating to End triggered by onDestroyGameEvent
        // The next state belonging to context GameStateMachineContext is outside the current context GameStateMachineContext
        context.setTransitionName("onDestroyGameEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineEndState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineEndState.getInstance());
        context.onEnd();
        return ;
    }

    /**
     * Event id: onEndGameEvent
     * 
     */
    public void onEndGameEvent(GameStateMachineContext context, EndGameEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onEndGameEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onEndGameEvent(event);
    }

    /**
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachineContext context, PlayerJoinGameRequestEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onPlayerJoinGameRequestEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onPlayerJoinGameRequestEvent(event);
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachineContext context, PlayerJoinGameSuccessEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onPlayerJoinGameSuccessEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onPlayerJoinGameSuccessEvent(event);
    }

    /**
     * Event id: onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(GameStateMachineContext context, PlayerJoinGameDeniedEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onPlayerJoinGameDeniedEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onPlayerJoinGameDeniedEvent(event);
    }

    /**
     * Event id: onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(GameStateMachineContext context, ChangePlayerLimitRequestEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onChangePlayerLimitRequestEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onChangePlayerLimitRequestEvent(event);
    }

    /**
     * Event id: onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(GameStateMachineContext context, ChangePlayerColorRequestEvent event) {
        GameStateMachineOperatingParallel parallelOperating = context.getGameStateMachineOperatingParallel();
        parallelOperating.getGameStateMachinePlayerHandlerContext().onChangePlayerColorRequestEvent(event);
        parallelOperating.getGameStateMachineGameHandlerContext().onChangePlayerColorRequestEvent(event);
    }

}
