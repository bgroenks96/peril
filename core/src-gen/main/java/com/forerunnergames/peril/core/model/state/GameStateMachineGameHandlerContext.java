
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.events.EndGameEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.stateforge.statemachine.context.AbstractContext;

public class GameStateMachineGameHandlerContext
    extends AbstractContext<GameStateMachineGameHandlerState, GameStateMachineContext>
{

    private com.forerunnergames.peril.core.model.GameModel _gameModel;

    /**
     * Context constructor
     * 
     */
    public GameStateMachineGameHandlerContext(com.forerunnergames.peril.core.model.GameModel gameModel, GameStateMachineContext contextParent) {
        super(contextParent);
        _gameModel = gameModel;
        setName("GameStateMachineGameHandlerContext");
        setInitialState(GameStateMachineWaitForGameToBeginState.getInstance());
    }

    public com.forerunnergames.peril.core.model.GameModel getGameModel() {
        return _gameModel;
    }

    /**
     * Enter the initial state
     * 
     */
    public void enterInitialState() {
        com.stateforge.statemachine.algorithm.StateOperation.walkTreeEntry(this, GameStateMachineGameHandlerState.getInstance(), GameStateMachineWaitForGameToBeginState.getInstance());
    }

    /**
     * Leave the current state
     * 
     */
    public void leaveCurrentState() {
        com.stateforge.statemachine.algorithm.StateOperation.walkTreeExit(this, this.getStateCurrent(), GameStateMachineGameHandlerState.getInstance());
    }

    /**
     * Event onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(CreateGameEvent event) {
        getStateCurrent().onCreateGameEvent(this, event);
    }

    /**
     * Event onDeterminePlayerTurnOrderCompleteEvent
     * 
     */
    public void onDeterminePlayerTurnOrderCompleteEvent(DeterminePlayerTurnOrderCompleteEvent event) {
        getStateCurrent().onDeterminePlayerTurnOrderCompleteEvent(this, event);
    }

    /**
     * Event onDistributeInitialArmiesCompleteEvent
     * 
     */
    public void onDistributeInitialArmiesCompleteEvent(DistributeInitialArmiesCompleteEvent event) {
        getStateCurrent().onDistributeInitialArmiesCompleteEvent(this, event);
    }

    /**
     * Event onEndGameEvent
     * 
     */
    public void onEndGameEvent(EndGameEvent event) {
        getStateCurrent().onEndGameEvent(this, event);
    }

    /**
     * Event onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(DestroyGameEvent event) {
        getStateCurrent().onDestroyGameEvent(this, event);
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
