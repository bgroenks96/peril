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

package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

import com.forerunnergames.peril.client.events.ConnectToServerDeniedEvent;
import com.forerunnergames.peril.client.events.ConnectToServerRequestEvent;
import com.forerunnergames.peril.client.events.ConnectToServerSuccessEvent;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.client.configuration.UnknownClientConfiguration;
import com.forerunnergames.tools.net.server.configuration.DefaultServerConfiguration;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener (references = References.Strong)
public final class DefaultJoinGameServerHandler implements JoinGameServerHandler
{
  private static final Logger log = LoggerFactory.getLogger (DefaultJoinGameServerHandler.class);
  private final MBassador <Event> eventBus;
  private final Set <PlayerPacket> players = new HashSet <> ();
  @Nullable
  private String playerName = null;
  @Nullable
  private JoinGameServerListener listener = null;
  @Nullable
  private GameServerConfiguration gameServerConfig = null;
  private ClientConfiguration clientConfig = new UnknownClientConfiguration ();
  private boolean joinGameIsInProgress = false;

  public DefaultJoinGameServerHandler (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  @Override
  public void join (final String playerName, final String serverAddress, final JoinGameServerListener listener)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNull (listener, "listener");

    this.playerName = playerName;
    this.listener = listener;

    players.clear ();

    eventBus.subscribe (this);

    final ConnectToServerRequestEvent event = new ConnectToServerRequestEvent (new DefaultServerConfiguration (
            serverAddress, NetworkSettings.DEFAULT_TCP_PORT));

    log.info ("Attempting to connect to server... [{}]", event);

    assert listener != null;
    listener.onJoinStart (playerName, event.getServerConfiguration ());

    eventBus.publishAsync (event);

    joinGameIsInProgress = true;
  }

  @Handler
  void onEvent (final ConnectToServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);
    log.info ("Successfully connected to server [{}]", event);
    log.info ("Attempting to join game server... [{}]", event);

    assert listener != null;
    listener.onConnectToServerSuccess (event.getServerConfiguration ());

    eventBus.publishAsync (new JoinGameServerRequestEvent ());
  }

  @Handler
  void onEvent (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);
    log.info ("Successfully joined game server [{}]", event);

    assert listener != null;
    listener.onJoinGameServerSuccess (event.getGameServerConfiguration (), event.getClientConfiguration ());

    gameServerConfig = event.getGameServerConfiguration ();
    clientConfig = event.getClientConfiguration ();

    assert playerName != null;
    final PlayerJoinGameRequestEvent playerJoinGameRequestEvent = new PlayerJoinGameRequestEvent (playerName);

    log.info ("Attempting to join game as a player... [{}]", playerJoinGameRequestEvent);

    eventBus.publishAsync (playerJoinGameRequestEvent);
  }

  @Handler
  void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);

    players.add (event.getPlayer ());

    assert playerName != null;
    if (event.getPlayer ().has (PersonIdentity.NON_SELF) || !event.getPlayer ().hasName (playerName))
    {
      log.debug ("Collecting [{}] for non-self player.", event);
      return;
    }

    log.info ("Successfully joined game as a player: [{}]", event);

    eventBus.unsubscribe (this);

    joinGameIsInProgress = false;

    assert listener != null;
    players.addAll (event.getPlayersInGame ());
    listener.onPlayerJoinGameSuccess (event.getPlayer (), event.getPlayersInGame ());
    listener.onJoinFinish (gameServerConfig, clientConfig, ImmutableSet.copyOf (players));
  }

  @Handler
  void onEvent (final ConnectToServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);
    log.error ("Could not connect to server: [{}]", event);

    eventBus.unsubscribe (this);

    joinGameIsInProgress = false;

    assert listener != null;
    listener.onConnectToServerFailure (event.getServerConfiguration (), event.getReason ());
  }

  @Handler
  void onEvent (final JoinGameServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);
    log.error ("Could not join game server: [{}]", event);

    eventBus.unsubscribe (this);

    joinGameIsInProgress = false;

    assert listener != null;
    listener.onJoinGameServerFailure (event.getClientConfiguration (), event.getReason ());
  }

  @Handler
  void onEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);

    assert playerName != null;
    if (!event.getPlayerName ().equals (playerName))
    {
      log.warn ("Received [{}] with player name [{}] while expecting player name [{}]", event, event.getPlayerName (),
                playerName);
      return;
    }

    log.error ("Could not join game as a player: [{}]", event);

    eventBus.unsubscribe (this);

    joinGameIsInProgress = false;

    assert listener != null;
    listener.onPlayerJoinGameFailure (event.getPlayerName (), event.getReason ());
  }
}
