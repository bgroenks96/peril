package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.core.model.state.GameStateMachine;
import com.forerunnergames.peril.core.shared.application.EventBasedApplication;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;

import net.engio.mbassy.bus.MBassador;

public final class ServerApplication extends EventBasedApplication
{
  private final GameStateMachine gameStateMachine;
  private final MBassador <Event> eventBus;

  public ServerApplication (final GameStateMachine gameStateMachine,
                            final MBassador <Event> eventBus,
                            final Controller... controllers)
  {
    super (controllers);

    Arguments.checkIsNotNull (gameStateMachine, "gameStateMachine");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.gameStateMachine = gameStateMachine;
    this.eventBus = eventBus;
  }

  @Override
  public void initialize ()
  {
    Runtime.getRuntime ().addShutdownHook (new Thread (new Runnable ()
    {
      @Override
      public void run ()
      {
        shutDown ();
      }
    }));

    super.initialize ();

    eventBus.subscribe (gameStateMachine);
  }
}
