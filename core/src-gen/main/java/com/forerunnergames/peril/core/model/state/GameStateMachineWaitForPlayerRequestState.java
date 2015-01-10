
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;

public class GameStateMachineWaitForPlayerRequestState
    extends GameStateMachinePlayerHandlerState
{

    private final static GameStateMachineWaitForPlayerRequestState instance = new GameStateMachineWaitForPlayerRequestState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineWaitForPlayerRequestState() {
        setName("WaitForPlayerRequest");
        setStateParent(GameStateMachinePlayerHandlerState.getInstance());
    }

    /**
     * Get the State Instance
     * 
     */
    public static GameStateMachinePlayerHandlerState getInstance() {
        return instance;
    }

    /**
     * onEntry
     * 
     */
    @Override
    public void onEntry(GameStateMachinePlayerHandlerContext context) {
        context.getObserver().onEntry(context.getName(), this.getName());
    }

    /**
     * onExit
     * 
     */
    @Override
    public void onExit(GameStateMachinePlayerHandlerContext context) {
        context.getObserver().onExit(context.getName(), this.getName());
    }

    /**
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachinePlayerHandlerContext context, PlayerJoinGameRequestEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Self transition triggered by onPlayerJoinGameRequestEvent
        context.setTransitionName("onPlayerJoinGameRequestEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineWaitForPlayerRequestState.getInstance());
        gameModel.handlePlayerJoinGameRequest               (event);
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineWaitForPlayerRequestState.getInstance());
        return ;
    }

    /**
     * Event id: onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(GameStateMachinePlayerHandlerContext context, ChangePlayerLimitRequestEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Self transition triggered by onChangePlayerLimitRequestEvent
        context.setTransitionName("onChangePlayerLimitRequestEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineWaitForPlayerRequestState.getInstance());
        gameModel.handleChangePlayerLimitRequest               (event);
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineWaitForPlayerRequestState.getInstance());
        return ;
    }

    /**
     * Event id: onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(GameStateMachinePlayerHandlerContext context, ChangePlayerColorRequestEvent event) {
        com.forerunnergames.peril.core.model.GameModel gameModel = context.getGameModel();
        // Self transition triggered by onChangePlayerColorRequestEvent
        context.setTransitionName("onChangePlayerColorRequestEvent");
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionBegin(context, GameStateMachineWaitForPlayerRequestState.getInstance());
        gameModel.handleChangePlayerColorRequest               (event);
        com.stateforge.statemachine.algorithm.StateOperation.processTransitionEnd(context, GameStateMachineWaitForPlayerRequestState.getInstance());
        return ;
    }

}
