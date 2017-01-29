/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.common.application.DefaultApplication;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

public final class ServerApplication extends DefaultApplication
{
  private final StateMachineEventHandler gameStateMachine;
  private final MBassador <Event> eventBus;
  private final AsyncExecution mainThreadExecutor;

  public ServerApplication (final StateMachineEventHandler gameStateMachine,
                            final MBassador <Event> eventBus,
                            final AsyncExecution mainThreadExecutor,
                            final Controller... controllers)
  {
    super (controllers);

    Arguments.checkIsNotNull (gameStateMachine, "gameStateMachine");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (mainThreadExecutor, "mainThreadExecutor");

    this.gameStateMachine = gameStateMachine;
    this.eventBus = eventBus;
    this.mainThreadExecutor = mainThreadExecutor;
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

    eventBus.subscribe (gameStateMachine);

    // Must come after subscribing GameStateMachine on the event bus
    // because this call generates state machine events.
    super.initialize ();
  }

  @Override
  public void update ()
  {
    super.update ();

    mainThreadExecutor.executeQueuedJobs ();
  }

  public void subscribe (final Object subscriber)
  {
    eventBus.subscribe (subscriber);
  }
}
