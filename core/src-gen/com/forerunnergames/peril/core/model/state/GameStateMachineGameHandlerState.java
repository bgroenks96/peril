
package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.peril.core.model.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.events.EndGameEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.stateforge.statemachine.state.AbstractState;

public class GameStateMachineGameHandlerState
    extends AbstractState<GameStateMachineGameHandlerContext, GameStateMachineGameHandlerState>
{

    private final static GameStateMachineGameHandlerState instance = new GameStateMachineGameHandlerState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineGameHandlerState() {
        setName("GameHandler");
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
     * Event id: onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(GameStateMachineGameHandlerContext context, CreateGameEvent event) {
    }

    /**
     * Event id: onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(GameStateMachineGameHandlerContext context, DestroyGameEvent event) {
    }

    /**
     * Event id: onEndGameEvent
     * 
     */
    public void onEndGameEvent(GameStateMachineGameHandlerContext context, EndGameEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachineGameHandlerContext context, PlayerJoinGameRequestEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachineGameHandlerContext context, PlayerJoinGameSuccessEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(GameStateMachineGameHandlerContext context, PlayerJoinGameDeniedEvent event) {
    }

    /**
     * Event id: onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(GameStateMachineGameHandlerContext context, ChangePlayerLimitRequestEvent event) {
    }

    /**
     * Event id: onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(GameStateMachineGameHandlerContext context, ChangePlayerColorRequestEvent event) {
    }

}
