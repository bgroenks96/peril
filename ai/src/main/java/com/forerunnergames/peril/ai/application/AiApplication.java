/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.ai.application;

import com.forerunnergames.peril.ai.controllers.AiController;
import com.forerunnergames.peril.ai.events.AiCommunicationEvent;
import com.forerunnergames.peril.ai.events.AiConnectionEvent;
import com.forerunnergames.peril.ai.events.AiDisconnectionEvent;
import com.forerunnergames.peril.ai.net.AiClient;
import com.forerunnergames.peril.common.application.DefaultApplication;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AiApplication extends DefaultApplication
{
  private static final Logger log = LoggerFactory.getLogger (AiApplication.class);
  private final GameServerConfiguration gameServerConfig;
  private final MBassador <Event> externalEventBus;
  private final MBassador <Event> internalEventBus;

  public AiApplication (final GameServerConfiguration gameServerConfig,
                        final MBassador <Event> externalEventBus,
                        final MBassador <Event> internalEventBus)
  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (externalEventBus, "externalEventBus");
    Arguments.checkIsNotNull (internalEventBus, "internalEventBus");

    this.gameServerConfig = gameServerConfig;
    this.externalEventBus = externalEventBus;
    this.internalEventBus = internalEventBus;
  }

  @Override
  public void initialize ()
  {
    for (int i = 1; i <= gameServerConfig.getPlayerLimitFor (PersonSentience.AI); ++i)
    {
      final String aiPlayerName = Strings.format ("DumbBot{}", i);
      final Controller aiController = new AiController (
              GameSettings.getAiPlayerNameWithMandatoryClanTag (aiPlayerName), gameServerConfig, internalEventBus);
      internalEventBus.subscribe (aiController);
      add (aiController);
    }

    externalEventBus.subscribe (this);
    internalEventBus.subscribe (this);

    super.initialize ();
  }

  @Override
  public void shutDown ()
  {
    super.shutDown ();

    externalEventBus.unsubscribe (this);
    internalEventBus.unsubscribe (this);
    externalEventBus.shutdown ();
    internalEventBus.shutdown ();
  }

  @Override
  public String getName ()
  {
    return getClass ().getSimpleName ();
  }

  @Handler
  public void onEvent (final AiConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    final String playerName = event.getPlayerName ();
    final Remote fakeClient = new AiClient (playerName);
    final Event joinGameServerRequest = new AiJoinGameServerRequestEvent ();

    log.info ("Creating fake server connection...");

    connectToServer (fakeClient);

    log.info ("Attempting to join game server...");

    sendToServer (joinGameServerRequest, fakeClient);
  }

  @Handler
  public void onEvent (final AiDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    final String playerName = event.getPlayerName ();
    disconnectFromServer (new AiClient (playerName));
    removeController (playerName);
  }

  @Handler
  public void onEvent (final AiCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    sendToServer (event);
  }

  private void connectToServer (final Remote fakeClient)
  {
    externalEventBus.publishAsync (new ClientConnectionEvent (fakeClient));
  }

  private void disconnectFromServer (final Remote fakeClient)
  {
    externalEventBus.publishAsync (new ClientDisconnectionEvent (fakeClient));
  }

  private void sendToServer (final AiCommunicationEvent event)
  {
    sendToServer (event.getMessage (), new AiClient (event.getPlayerName ()));
  }

  private void sendToServer (final Event message, final Remote fakeClient)
  {
    externalEventBus.publishAsync (new ClientCommunicationEvent (message, fakeClient));
  }
}
