
package com.forerunnergames.peril.core.model.state;


public class GameStateMachineHandlePlayerJoinGameRequestState
    extends GameStateMachineRootState
{

    private final static GameStateMachineHandlePlayerJoinGameRequestState instance = new GameStateMachineHandlePlayerJoinGameRequestState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineHandlePlayerJoinGameRequestState() {
        setName("HandlePlayerJoinGameRequest");
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
        model.handlePlayerJoinGameRequest();
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
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachineContext context) {
        GameModel model = context.getGameModel();
        // Self transition triggered by onPlayerJoinGameRequestEvent
        context.setTransitionName("onPlayerJoinGameRequestEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineHandlePlayerJoinGameRequestState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineHandlePlayerJoinGameRequestState.getInstance());
        return ;
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachineContext context) {
        GameModel model = context.getGameModel();
        // Transition from HandlePlayerJoinGameRequest to DeterminePlayerTurnOrder triggered by onPlayerJoinGameSuccessEvent[model.isGameFull()]
        // The next state is within the context GameStateMachineContext
        if ((model.isGameFull())) {
            context.setTransitionName("onPlayerJoinGameSuccessEvent[model.isGameFull()]");
            com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineDeterminePlayerTurnOrderState.getInstance());
            com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineDeterminePlayerTurnOrderState.getInstance());
            return ;
        }
        // Transition from HandlePlayerJoinGameRequest to WaitForPlayerJoinGameRequest triggered by onPlayerJoinGameSuccessEvent
        // The next state is within the context GameStateMachineContext
        context.setTransitionName("onPlayerJoinGameSuccessEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineWaitForPlayerJoinGameRequestState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineWaitForPlayerJoinGameRequestState.getInstance());
        return ;
    }

    /**
     * Event id: onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(GameStateMachineContext context) {
        GameModel model = context.getGameModel();
        // Transition from HandlePlayerJoinGameRequest to WaitForPlayerJoinGameRequest triggered by onPlayerJoinGameDeniedEvent
        // The next state is within the context GameStateMachineContext
        context.setTransitionName("onPlayerJoinGameDeniedEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineWaitForPlayerJoinGameRequestState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineWaitForPlayerJoinGameRequestState.getInstance());
        return ;
    }

}
