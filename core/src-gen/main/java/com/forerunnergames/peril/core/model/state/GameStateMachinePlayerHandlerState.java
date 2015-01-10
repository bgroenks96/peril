
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
import com.stateforge.statemachine.state.AbstractState;

public class GameStateMachinePlayerHandlerState
    extends AbstractState<GameStateMachinePlayerHandlerContext, GameStateMachinePlayerHandlerState>
{

    private final static GameStateMachinePlayerHandlerState instance = new GameStateMachinePlayerHandlerState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachinePlayerHandlerState() {
        setName("PlayerHandler");
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
     * Event id: onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(GameStateMachinePlayerHandlerContext context, CreateGameEvent event) {
    }

    /**
     * Event id: onDeterminePlayerTurnOrderCompleteEvent
     * 
     */
    public void onDeterminePlayerTurnOrderCompleteEvent(GameStateMachinePlayerHandlerContext context, DeterminePlayerTurnOrderCompleteEvent event) {
    }

    /**
     * Event id: onDistributeInitialArmiesCompleteEvent
     * 
     */
    public void onDistributeInitialArmiesCompleteEvent(GameStateMachinePlayerHandlerContext context, DistributeInitialArmiesCompleteEvent event) {
    }

    /**
     * Event id: onEndGameEvent
     * 
     */
    public void onEndGameEvent(GameStateMachinePlayerHandlerContext context, EndGameEvent event) {
    }

    /**
     * Event id: onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(GameStateMachinePlayerHandlerContext context, DestroyGameEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachinePlayerHandlerContext context, PlayerJoinGameRequestEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachinePlayerHandlerContext context, PlayerJoinGameSuccessEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(GameStateMachinePlayerHandlerContext context, PlayerJoinGameDeniedEvent event) {
    }

    /**
     * Event id: onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(GameStateMachinePlayerHandlerContext context, ChangePlayerLimitRequestEvent event) {
    }

    /**
     * Event id: onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(GameStateMachinePlayerHandlerContext context, ChangePlayerColorRequestEvent event) {
    }

}
