
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

public class GameStateMachineEndState
    extends GameStateMachineRootState
{

    private final static GameStateMachineEndState instance = new GameStateMachineEndState();

    /**
     * Protected Constructor
     * 
     */
    protected GameStateMachineEndState() {
        setName("End");
        setStateParent(GameStateMachineRootState.getInstance());
    }

    /**
     * Get the State Instance
     * 
     */
    public static GameStateMachineRootState getInstance() {
        return instance;
    }

    /**
     * onEntry
     * 
     */
    @Override
    public void onEntry(GameStateMachineContext context) {
        context.getObserver().onEntry(context.getName(), this.getName());
    }

    /**
     * onExit
     * 
     */
    @Override
    public void onExit(GameStateMachineContext context) {
        context.getObserver().onExit(context.getName(), this.getName());
    }

    /**
     * Event id: onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(GameStateMachineContext context, CreateGameEvent event) {
    }

    /**
     * Event id: onDeterminePlayerTurnOrderCompleteEvent
     * 
     */
    public void onDeterminePlayerTurnOrderCompleteEvent(GameStateMachineContext context, DeterminePlayerTurnOrderCompleteEvent event) {
    }

    /**
     * Event id: onDistributeInitialArmiesCompleteEvent
     * 
     */
    public void onDistributeInitialArmiesCompleteEvent(GameStateMachineContext context, DistributeInitialArmiesCompleteEvent event) {
    }

    /**
     * Event id: onEndGameEvent
     * 
     */
    public void onEndGameEvent(GameStateMachineContext context, EndGameEvent event) {
    }

    /**
     * Event id: onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(GameStateMachineContext context, DestroyGameEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(GameStateMachineContext context, PlayerJoinGameRequestEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(GameStateMachineContext context, PlayerJoinGameSuccessEvent event) {
    }

    /**
     * Event id: onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(GameStateMachineContext context, PlayerJoinGameDeniedEvent event) {
    }

    /**
     * Event id: onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(GameStateMachineContext context, ChangePlayerLimitRequestEvent event) {
    }

    /**
     * Event id: onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(GameStateMachineContext context, ChangePlayerColorRequestEvent event) {
    }

}
