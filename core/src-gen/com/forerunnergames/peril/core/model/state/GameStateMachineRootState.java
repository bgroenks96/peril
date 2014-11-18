
package com.forerunnergames.peril.core.model.state;

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
     * Event id: onCreateNewGameEvent
     * 
     */
    public void onCreateNewGameEvent(GameStateMachineContext context) {
    }

    /**
     * Event id: onDeterminePlayerTurnOrderComplete
     * 
     */
    public void onDeterminePlayerTurnOrderComplete(GameStateMachineContext context) {
    }

    /**
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachineContext context) {
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachineContext context) {
    }

    /**
     * Event id: onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(GameStateMachineContext context) {
    }

}
