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

package com.forerunnergames.peril.client.net;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.AbstractClientController;
import com.forerunnergames.tools.net.client.Client;
import com.forerunnergames.tools.net.events.local.ServerCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ServerConnectionEvent;
import com.forerunnergames.tools.net.events.local.ServerDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBasedClientController extends AbstractClientController
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedClientController.class);
  private final MBassador <Event> eventBus;
  private final AsyncExecution mainThreadExecutor;

  public EventBasedClientController (final Client client,
                                     final ImmutableSet <Class <?>> classesToRegisterForNetworkSerialization,
                                     final MBassador <Event> eventBus,
                                     final AsyncExecution mainThreadExecutor)
  {
    super (client, classesToRegisterForNetworkSerialization);

    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (mainThreadExecutor, "mainThreadExecutor");

    this.eventBus = eventBus;
    this.mainThreadExecutor = mainThreadExecutor;
  }

  @Override
  protected void onConnectionTo (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Connected to server [{}].", server);

        eventBus.publish (new ServerConnectionEvent (server));
      }
    });
  }

  @Override
  protected void onDisconnectionFrom (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Disconnected from server [{}].", server);

        eventBus.publish (new ServerDisconnectionEvent (server));
      }
    });
  }

  @Override
  protected void onCommunication (final Object object, final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.debug ("Received object [{}] from server [{}].", object, server);

        if (!(object instanceof ServerEvent))
        {
          log.warn ("Ignoring unrecognized object [{}] from server [{}].", object, server);

          return;
        }

        eventBus.publish (new ServerCommunicationEvent ((Event) object, server));
      }
    });
  }
}
