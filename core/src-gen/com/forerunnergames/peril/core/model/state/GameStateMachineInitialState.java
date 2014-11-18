
package com.forerunnergames.peril.core.model.state;


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
     * Event id: onCreateNewGameEvent
     * 
     */
    public void onCreateNewGameEvent(GameStateMachineContext context) {
        GameModel model = context.getGameModel();
        // Transition from Initial to WaitForPlayerJoinGameRequest triggered by onCreateNewGameEvent
        // The next state is within the context GameStateMachineContext
        context.setTransitionName("onCreateNewGameEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineWaitForPlayerJoinGameRequestState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineWaitForPlayerJoinGameRequestState.getInstance());
        return ;
    }

}
