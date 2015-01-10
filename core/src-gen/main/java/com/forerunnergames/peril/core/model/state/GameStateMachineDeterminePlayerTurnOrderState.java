
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;

public class GameStateMachineDeterminePlayerTurnOrderState
    extends GameStateMachineGameHandlerState
{

    private final static GameStateMachineDeterminePlayerTurnOrderState instance = new GameStateMachineDeterminePlayerTurnOrderState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineDeterminePlayerTurnOrderState() {
        setName("DeterminePlayerTurnOrder");
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
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        gameModel.determinePlayerTurnOrder();
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
     * Event id: onDeterminePlayerTurnOrderCompleteEvent
     * 
     */
    public void onDeterminePlayerTurnOrderCompleteEvent(GameStateMachineGameHandlerContext context, DeterminePlayerTurnOrderCompleteEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Transition from DeterminePlayerTurnOrder to DistributeInitialArmies triggered by onDeterminePlayerTurnOrderCompleteEvent
        // The next state is within the context GameStateMachineGameHandlerContext
        context.setTransitionName("onDeterminePlayerTurnOrderCompleteEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineDistributeInitialArmiesState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineDistributeInitialArmiesState.getInstance());
        return ;
    }

}
