
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.events.EndGameEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.stateforge.statemachine.context.AbstractContext;

public class GameStateMachinePlayerHandlerContext
    extends AbstractContext<GameStateMachinePlayerHandlerState, GameStateMachineContext>
{

    private com.forerunnergames.peril.core.model.GameModel _gameModel;

    /**
     * Context constructor
     * 
     */
    public GameStateMachinePlayerHandlerContext(com.forerunnergames.peril.core.model.GameModel gameModel, GameStateMachineContext contextParent) {
        super(contextParent);
        _gameModel = gameModel;
        setName("GameStateMachinePlayerHandlerContext");
        setInitialState(GameStateMachineWaitForPlayerRequestState.getInstance());
    }

    public com.forerunnergames.peril.core.model.GameModel getGameModel() {
        return _gameModel;
    }

    /**
     * Enter the initial state
     * 
     */
    public void enterInitialState() {
        com.stateforge.statemachine.algorithm.StateOperation.walkTreeEntry(this, GameStateMachinePlayerHandlerState.getInstance(), GameStateMachineWaitForPlayerRequestState.getInstance());
    }

    /**
     * Leave the current state
     * 
     */
    public void leaveCurrentState() {
        com.stateforge.statemachine.algorithm.StateOperation.walkTreeExit(this, this.getStateCurrent(), GameStateMachinePlayerHandlerState.getInstance());
    }

    /**
     * Event onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(CreateGameEvent event) {
        getStateCurrent().onCreateGameEvent(this, event);
    }

    /**
     * Event onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(DestroyGameEvent event) {
        getStateCurrent().onDestroyGameEvent(this, event);
    }

    /**
     * Event onEndGameEvent
     * 
     */
    public void onEndGameEvent(EndGameEvent event) {
        getStateCurrent().onEndGameEvent(this, event);
    }

    /**
     * Event onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(PlayerJoinGameRequestEvent event) {
        getStateCurrent().onPlayerJoinGameRequestEvent(this, event);
    }

    /**
     * Event onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(PlayerJoinGameSuccessEvent event) {
        getStateCurrent().onPlayerJoinGameSuccessEvent(this, event);
    }

    /**
     * Event onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(PlayerJoinGameDeniedEvent event) {
        getStateCurrent().onPlayerJoinGameDeniedEvent(this, event);
    }

    /**
     * Event onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(ChangePlayerLimitRequestEvent event) {
        getStateCurrent().onChangePlayerLimitRequestEvent(this, event);
    }

    /**
     * Event onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(ChangePlayerColorRequestEvent event) {
        getStateCurrent().onChangePlayerColorRequestEvent(this, event);
    }

}
