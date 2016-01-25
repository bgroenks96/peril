/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.integration.server;

import com.forerunnergames.peril.server.application.ServerApplication;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.tools.common.Arguments;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestServerApplication
{
  private final int CLIENT_CONNECTION_TIMEOUT_MS = 300000;
  private final MultiplayerController mpc;
  private final ServerApplication serverApplication;
  private final AtomicBoolean shouldShutDown = new AtomicBoolean ();

  public TestServerApplication (final ServerApplication serverApplication, final MultiplayerController mpc)
  {
    Arguments.checkIsNotNull (serverApplication, "serverApplication");
    Arguments.checkIsNotNull (mpc, "mpc");

    this.serverApplication = serverApplication;
    this.mpc = mpc;
  }

  public void start ()
  {
    serverApplication.initialize ();
    mpc.initialize ();
    mpc.setClientConnectTimeout (CLIENT_CONNECTION_TIMEOUT_MS);
    Executors.newSingleThreadExecutor ().execute (new ServerUpdateLoop ());
  }

  public void shutDown ()
  {
    shouldShutDown.set (true);
  }

  public MultiplayerController getMultiplayerController ()
  {
    return mpc;
  }

  private class ServerUpdateLoop implements Runnable
  {
    @Override
    public void run ()
    {
      while (!shouldShutDown.get ())
      {
        serverApplication.update ();
        Thread.yield ();
      }
      serverApplication.shutDown ();
      mpc.shutDown ();
    }
  }
}
