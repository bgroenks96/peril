
package com.forerunnergames.peril.core.model.state;


public class GameStateMachineDeterminePlayerTurnOrderState
    extends GameStateMachineRootState
{

    private final static GameStateMachineDeterminePlayerTurnOrderState instance = new GameStateMachineDeterminePlayerTurnOrderState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineDeterminePlayerTurnOrderState() {
        setName("DeterminePlayerTurnOrder");
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
        GameModel model = context.getGameModel();
        model.determinePlayerTurnOrder();
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
     * Event id: onDeterminePlayerTurnOrderComplete
     * 
     */
    public void onDeterminePlayerTurnOrderComplete(GameStateMachineContext context) {
        GameModel model = context.getGameModel();
        // Transition from DeterminePlayerTurnOrder to End triggered by onDeterminePlayerTurnOrderComplete
        // The next state is within the context GameStateMachineContext
        context.setTransitionName("onDeterminePlayerTurnOrderComplete");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineEndState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineEndState.getInstance());
        context.onEnd();
        return ;
    }

}
