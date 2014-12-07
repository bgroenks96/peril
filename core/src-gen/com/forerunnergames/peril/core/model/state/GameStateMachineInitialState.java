
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateGameEvent;

public class GameStateMachineInitialState
    extends GameStateMachineRootState
{

    private final static GameStateMachineInitialState instance = new GameStateMachineInitialState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineInitialState() {
        setName("Initial");
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
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Transition from Initial to Operating triggered by onCreateGameEvent
        // The next state belonging to context GameStateMachineContext is outside the current context GameStateMachineContext
        context.setTransitionName("onCreateGameEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineOperatingState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineOperatingState.getInstance());
        return ;
    }

}
