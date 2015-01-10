
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

public class GameStateMachineContext
    extends AbstractContext<GameStateMachineRootState, GameStateMachineContext>
{

    private com.forerunnergames.peril.core.model.GameModel _gameModel;
    private GameStateMachineOperatingParallel _parallelOperating;

    /**
     * Context constructor
     * 
     */
    public GameStateMachineContext(com.forerunnergames.peril.core.model.GameModel gameModel) {
        super();
        _gameModel = gameModel;
        setName("GameStateMachineContext");
        setInitialState(GameStateMachineInitialState.getInstance());
        _parallelOperating = new GameStateMachineOperatingParallel(this, gameModel);
    }

    public com.forerunnergames.peril.core.model.GameModel getGameModel() {
        return _gameModel;
    }

    /**
     * Enter the initial state
     * 
     */
    public void enterInitialState() {
        com.stateforge.statemachine.algorithm.StateOperation.walkTreeEntry(this, GameStateMachineRootState.getInstance(), GameStateMachineInitialState.getInstance());
    }

    /**
     * Leave the current state
     * 
     */
    public void leaveCurrentState() {
        com.stateforge.statemachine.algorithm.StateOperation.walkTreeExit(this, this.getStateCurrent(), GameStateMachineRootState.getInstance());
    }

    /**
     * Asynchronous event onCreateGameEvent
     * 
     */
    public void onCreateGameEvent(final CreateGameEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onCreateGameEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onDeterminePlayerTurnOrderCompleteEvent
     * 
     */
    public void onDeterminePlayerTurnOrderCompleteEvent(final DeterminePlayerTurnOrderCompleteEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onDeterminePlayerTurnOrderCompleteEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onDistributeInitialArmiesCompleteEvent
     * 
     */
    public void onDistributeInitialArmiesCompleteEvent(final DistributeInitialArmiesCompleteEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onDistributeInitialArmiesCompleteEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onEndGameEvent
     * 
     */
    public void onEndGameEvent(final EndGameEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onEndGameEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onDestroyGameEvent
     * 
     */
    public void onDestroyGameEvent(final DestroyGameEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onDestroyGameEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onPlayerJoinGameRequestEvent
     * 
     */
    public void onPlayerJoinGameRequestEvent(final PlayerJoinGameRequestEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onPlayerJoinGameRequestEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onPlayerJoinGameSuccessEvent
     * 
     */
    public void onPlayerJoinGameSuccessEvent(final PlayerJoinGameSuccessEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onPlayerJoinGameSuccessEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onPlayerJoinGameDeniedEvent
     * 
     */
    public void onPlayerJoinGameDeniedEvent(final PlayerJoinGameDeniedEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onPlayerJoinGameDeniedEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onChangePlayerLimitRequestEvent
     * 
     */
    public void onChangePlayerLimitRequestEvent(final ChangePlayerLimitRequestEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onChangePlayerLimitRequestEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onChangePlayerColorRequestEvent
     * 
     */
    public void onChangePlayerColorRequestEvent(final ChangePlayerColorRequestEvent event) {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onChangePlayerColorRequestEvent(me, event);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    public GameStateMachineOperatingParallel getGameStateMachineOperatingParallel() {
        return _parallelOperating;
    }

}
