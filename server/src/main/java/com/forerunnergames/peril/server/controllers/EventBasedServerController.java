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

package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientEvent;
import com.forerunnergames.tools.net.server.AbstractServerController;
import com.forerunnergames.tools.net.server.Server;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBasedServerController extends AbstractServerController
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedServerController.class);
  private final MBassador <Event> eventBus;
  private final AsyncExecution mainThreadExecutor;

  public EventBasedServerController (final Server server,
                                     final int serverTcpPort,
                                     final ImmutableSet <Class <?>> classesToRegisterForNetworkSerialization,
                                     final MBassador <Event> eventBus,
                                     final AsyncExecution mainThreadExecutor)
  {
    super (server, serverTcpPort, classesToRegisterForNetworkSerialization);

    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (mainThreadExecutor, "mainThreadExecutor");

    this.eventBus = eventBus;
    this.mainThreadExecutor = mainThreadExecutor;
  }

  @Override
  protected void onConnection (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Client [{}] connected.", client);

        eventBus.publish (new ClientConnectionEvent (client));
      }
    });
  }

  @Override
  protected void onDisconnection (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Client [{}] disconnected.", client);

        eventBus.publish (new ClientDisconnectionEvent (client));
      }
    });
  }

  @Override
  protected void onCommunication (final Object object, final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.debug ("Received object [{}] from client [{}].", object, client);

        if (!(object instanceof ClientEvent))
        {
          log.debug ("Ignoring unrecognized object [{}] from client [{}].", object, client);

          return;
        }

        eventBus.publish (new ClientCommunicationEvent ((ClientEvent) object, client));
      }
    });
  }
}
