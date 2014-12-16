
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateGameEvent;

public class GameStateMachineEndGameState
    extends GameStateMachineGameHandlerState
{

    private final static GameStateMachineEndGameState instance = new GameStateMachineEndGameState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineEndGameState() {
        setName("EndGame");
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
        gameModel.endGame();
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
     * Event id: onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(GameStateMachineGameHandlerContext context, CreateGameEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Transition from EndGame to WaitForGameToBegin triggered by onCreateGameEvent
        // The next state is within the context GameStateMachineGameHandlerContext
        context.setTransitionName("onCreateGameEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineWaitForGameToBeginState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineWaitForGameToBeginState.getInstance());
        return ;
    }

}
