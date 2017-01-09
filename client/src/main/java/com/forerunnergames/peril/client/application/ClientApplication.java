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

package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimerUtils;

import com.forerunnergames.peril.common.application.DefaultApplication;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.Controller;

import de.matthiasmann.AsyncExecution;

public final class ClientApplication extends DefaultApplication
{
  private final AsyncExecution mainThreadExecutor;

  public ClientApplication (final AsyncExecution mainThreadExecutor, final Controller... controllers)
  {
    super (controllers);

    Arguments.checkIsNotNull (mainThreadExecutor, "mainThreadExecutor");

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
        mainThreadExecutor.invokeLater (new Runnable ()
        {
          @Override
          public void run ()
          {
            shutDown ();
          }
        });
      }
    }));

    TimerUtils.allowTimerToRunInBackgroundWindow ();

    super.initialize ();
  }

  @Override
  public void update ()
  {
    super.update ();

    if (shouldShutDown ()) Gdx.app.exit ();

    mainThreadExecutor.executeQueuedJobs ();
  }

  @Override
  public void shutDown ()
  {
    super.shutDown ();

    TimerUtils.disposeTimerThread ();
  }
}
