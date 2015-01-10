
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;

public class GameStateMachineDistributeInitialArmiesState
    extends GameStateMachineGameHandlerState
{

    private final static GameStateMachineDistributeInitialArmiesState instance = new GameStateMachineDistributeInitialArmiesState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineDistributeInitialArmiesState() {
        setName("DistributeInitialArmies");
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
        gameModel.distributeInitialArmies();
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
     * Event id: onDistributeInitialArmiesCompleteEvent
     * 
     */
    public void onDistributeInitialArmiesCompleteEvent(GameStateMachineGameHandlerContext context, DistributeInitialArmiesCompleteEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Transition from DistributeInitialArmies to EndGame triggered by onDistributeInitialArmiesCompleteEvent
        // The next state is within the context GameStateMachineGameHandlerContext
        context.setTransitionName("onDistributeInitialArmiesCompleteEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineEndGameState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineEndGameState.getInstance());
        return ;
    }

}
