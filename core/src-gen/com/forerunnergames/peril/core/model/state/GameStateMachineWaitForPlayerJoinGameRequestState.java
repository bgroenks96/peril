
package com.forerunnergames.peril.core.model.state;


public class GameStateMachineWaitForPlayerJoinGameRequestState
    extends GameStateMachineRootState
{

    private final static GameStateMachineWaitForPlayerJoinGameRequestState instance = new GameStateMachineWaitForPlayerJoinGameRequestState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineWaitForPlayerJoinGameRequestState() {
        setName("WaitForPlayerJoinGameRequest");
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
        model.waitForPlayerJoinGameRequest();
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
        // Transition from WaitForPlayerJoinGameRequest to HandlePlayerJoinGameRequest triggered by onPlayerJoinGameRequestEvent
        // The next state is within the context GameStateMachineContext
        context.setTransitionName("onPlayerJoinGameRequestEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineHandlePlayerJoinGameRequestState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineHandlePlayerJoinGameRequestState.getInstance());
        return ;
    }

}
