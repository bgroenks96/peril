
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;

public class GameStateMachineWaitForGameToBeginState
    extends GameStateMachineGameHandlerState
{

    private final static GameStateMachineWaitForGameToBeginState instance = new GameStateMachineWaitForGameToBeginState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineWaitForGameToBeginState() {
        setName("WaitForGameToBegin");
        setStateParent(GameStateMachineGameHandlerState.getInstance());
    }

    /**
     * Get the State Instance
     * 
     */
    public static GameStateMachineGameHandlerState getInstance() {
        return instance;
    }

    /**
     * onEntry
     * 
     */
    @Override
    public void onEntry(GameStateMachineGameHandlerContext context) {
        context.getObserver().onEntry(context.getName(), this.getName());
    }

    /**
     * onExit
     * 
     */
    @Override
    public void onExit(GameStateMachineGameHandlerContext context) {
        context.getObserver().onExit(context.getName(), this.getName());
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachineGameHandlerContext context, PlayerJoinGameSuccessEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Transition from WaitForGameToBegin to DeterminePlayerTurnOrder triggered by onPlayerJoinGameSuccessEvent[gameModel.isGameFull()]
        // The next state is within the context GameStateMachineGameHandlerContext
        if ((gameModel.isGameFull())) {
            context.setTransitionName("onPlayerJoinGameSuccessEvent[gameModel.isGameFull()]");
            com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineDeterminePlayerTurnOrderState.getInstance());
            com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineDeterminePlayerTurnOrderState.getInstance());
            return ;
        }
    }

}
