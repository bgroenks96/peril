
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.EndGameEvent;

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
     * Event id: onEndGameEvent
     * 
     */
    public void onEndGameEvent(GameStateMachineGameHandlerContext context, EndGameEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Transition from DeterminePlayerTurnOrder to EndGame triggered by onEndGameEvent
        // The next state is within the context GameStateMachineGameHandlerContext
        context.setTransitionName("onEndGameEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineEndGameState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineEndGameState.getInstance());
        return ;
    }

}
