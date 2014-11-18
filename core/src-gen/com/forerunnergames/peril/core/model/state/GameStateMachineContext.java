
package com.forerunnergames.peril.core.model.state;

import com.stateforge.statemachine.context.AbstractContext;

public class GameStateMachineContext
    extends AbstractContext<GameStateMachineRootState, GameStateMachineContext>
{

    private GameModel _model;

    /**
     * Context constructor
     * 
     */
    public GameStateMachineContext(GameModel model) {
        super();
        _model = model;
        setName("GameStateMachineContext");
        setInitialState(GameStateMachineInitialState.getInstance());
    }

    public GameModel getGameModel() {
        return _model;
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
     * Asynchronous event onCreateNewGameEvent
     * 
     */
    public void onCreateNewGameEvent() {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onCreateNewGameEvent(me);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

    /**
     * Asynchronous event onDeterminePlayerTurnOrderComplete
     * 
     */
    public void onDeterminePlayerTurnOrderComplete() {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onDeterminePlayerTurnOrderComplete(me);
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
    public void onPlayerJoinGameRequestEvent() {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onPlayerJoinGameRequestEvent(me);
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
    public void onPlayerJoinGameSuccessEvent() {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onPlayerJoinGameSuccessEvent(me);
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
    public void onPlayerJoinGameDeniedEvent() {
        final GameStateMachineContext me = this;
        getExecutorService().execute(new Runnable() {


            public void run() {
                try {
                    getStateCurrent().onPlayerJoinGameDeniedEvent(me);
                } catch (Exception exception) {
                    onEnd(exception);
                }
            }

        }
        );
    }

}
