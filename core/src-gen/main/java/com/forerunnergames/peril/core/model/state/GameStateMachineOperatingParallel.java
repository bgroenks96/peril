
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.GameModel;
import com.stateforge.statemachine.context.ContextParallel;


/**
 * Parallel class com.forerunnergames.peril.core.model.state.GameStateMachineOperatingParallel
 * 
 */
public class GameStateMachineOperatingParallel
    extends ContextParallel
{

    private GameStateMachineContext context;
    private GameStateMachinePlayerHandlerContext myGameStateMachinePlayerHandlerContext;
    private GameStateMachineGameHandlerContext myGameStateMachineGameHandlerContext;

    public GameStateMachineOperatingParallel(GameStateMachineContext contextToSet, GameModel gameModel) {
        context = contextToSet;
        myGameStateMachinePlayerHandlerContext = new GameStateMachinePlayerHandlerContext(gameModel, contextToSet);
        myGameStateMachineGameHandlerContext = new GameStateMachineGameHandlerContext(gameModel, contextToSet);
    }

    public GameStateMachinePlayerHandlerContext getGameStateMachinePlayerHandlerContext() {
        return myGameStateMachinePlayerHandlerContext;
    }

    public GameStateMachineGameHandlerContext getGameStateMachineGameHandlerContext() {
        return myGameStateMachineGameHandlerContext;
    }

    /**
     * Transition to next the next
     * 
     */
    public void transitionToNextState() {
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineEndState.getInstance());
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineEndState.getInstance());
        context.onEnd();
    }

}
