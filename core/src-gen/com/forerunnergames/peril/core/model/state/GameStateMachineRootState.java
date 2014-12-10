
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.events.EndGameEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.stateforge.statemachine.state.AbstractState;

public class GameStateMachineRootState
    extends AbstractState<GameStateMachineContext, GameStateMachineRootState>
{

    private final static GameStateMachineRootState instance = new GameStateMachineRootState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineRootState() {
        setName("Root");
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
    }

    /**
     * onExit
     * 
     */
    @Override
    public void onExit(GameStateMachineContext context) {
        context.getObserver().onExit(context.getName(), this.getName());
    }

    /**
     * Event id: onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(GameStateMachineContext context, CreateGameEvent event) {
    }

    /**
     * Event id: onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(GameStateMachineContext context, DestroyGameEvent event) {
    }

    /**
     * Event id: onEndGameEvent
     * 
     */
    public void onEndGameEvent(GameStateMachineContext context, EndGameEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachineContext context, PlayerJoinGameRequestEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachineContext context, PlayerJoinGameSuccessEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(GameStateMachineContext context, PlayerJoinGameDeniedEvent event) {
    }

    /**
     * Event id: onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(GameStateMachineContext context, ChangePlayerLimitRequestEvent event) {
    }

    /**
     * Event id: onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(GameStateMachineContext context, ChangePlayerColorRequestEvent event) {
    }

}