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

package com.forerunnergames.peril.ai.controllers;

import com.forerunnergames.peril.ai.events.AiCommunicationEvent;
import com.forerunnergames.peril.ai.events.AiConnectionEvent;
import com.forerunnergames.peril.ai.events.AiDisconnectionEvent;
import com.forerunnergames.peril.ai.net.AiJoinGameServerHandler;
import com.forerunnergames.peril.ai.processors.AiProcessor;
import com.forerunnergames.peril.ai.processors.ChatProcessor;
import com.forerunnergames.peril.ai.processors.GameLogicProcessor;
import com.forerunnergames.peril.common.JoinGameServerHandler;
import com.forerunnergames.peril.common.JoinGameServerListenerAdapter;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.client.request.AiPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.WaitingForPlayersToJoinGameEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AiController extends ControllerAdapter
{
  private static final Logger log = LoggerFactory.getLogger (AiController.class);
  private final String playerName;
  private final GameServerConfiguration gameServerConfig;
  private final AiProcessor chatProcessor;
  private final AiProcessor gameLogicProcessor;
  private final JoinGameServerHandler joinGameServerHandler;
  private final MBassador <Event> internalEventBus;

  public AiController (final String playerName,
                       final GameServerConfiguration gameServerConfig,
                       final MBassador <Event> internalEventBus)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (internalEventBus, "internalEventBus");

    this.playerName = playerName;
    this.gameServerConfig = gameServerConfig;
    this.internalEventBus = internalEventBus;
    joinGameServerHandler = new AiJoinGameServerHandler (internalEventBus);
    chatProcessor = new ChatProcessor (playerName, gameServerConfig, internalEventBus);
    gameLogicProcessor = new GameLogicProcessor (playerName, gameServerConfig, internalEventBus);
  }

  @Override
  public String getName ()
  {
    return playerName;
  }

  @Override
  public void shutDown ()
  {
    gameLogicProcessor.deactivate ();
    chatProcessor.deactivate ();
  }

  @Handler
  public void onEvent (final WaitingForPlayersToJoinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("[{}] received event: [{}].", playerName, event);

    joinGameServerHandler.join (playerName, gameServerConfig.getServerAddress (), new JoinGameServerListenerAdapter ()
    {
      @Override
      public void onJoinStart (final String playerName, final ServerConfiguration config)
      {
        Arguments.checkIsNotNull (playerName, "playerName");
        Arguments.checkIsNotNull (config, "config");

        assert playerName.equals (AiController.this.playerName);

        internalEventBus.publish (new AiConnectionEvent (playerName));
      }

      @Override
      public void onJoinGameServerSuccess (final String playerName, final JoinGameServerSuccessEvent event)
      {
        Arguments.checkIsNotNull (playerName, "playerName");
        Arguments.checkIsNotNull (event, "event");

        assert playerName.equals (AiController.this.playerName);

        internalEventBus.publish (new AiCommunicationEvent (new AiPlayerJoinGameRequestEvent (playerName), playerName));
      }

      @Override
      public void onJoinGameServerFailure (final String playerName, final JoinGameServerDeniedEvent event)
      {
        Arguments.checkIsNotNull (playerName, "playerName");
        Arguments.checkIsNotNull (event, "event");

        assert playerName.equals (AiController.this.playerName);

        internalEventBus.publish (new AiDisconnectionEvent (playerName));
      }

      @Override
      public void onPlayerJoinGameFailure (final PlayerJoinGameDeniedEvent event)
      {
        Arguments.checkIsNotNull (event, "event");

        assert event.getPlayerName ().equals (playerName);

        internalEventBus.publish (new AiDisconnectionEvent (event.getPlayerName ()));
      }

      @Override
      public void onJoinFinish (final GameServerConfiguration gameServerConfig,
                                final ClientConfiguration clientConfig,
                                final ImmutableSet <PlayerPacket> players,
                                final PlayerJoinGameSuccessEvent event)
      {
        Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
        Arguments.checkIsNotNull (clientConfig, "clientConfig");
        Arguments.checkIsNotNull (players, "players");
        Arguments.checkHasNoNullElements (players, "players");
        Arguments.checkIsNotNull (event, "event");

        assert event.getPlayerName ().equals (playerName);
        assert event.hasIdentity (PersonIdentity.SELF);

        gameLogicProcessor.activate ();
        chatProcessor.activate ();

        internalEventBus.publish (event);
      }
    });
  }
}
