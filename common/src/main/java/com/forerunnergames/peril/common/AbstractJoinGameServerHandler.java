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

package com.forerunnergames.peril.common;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
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
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener (references = References.Strong)
public abstract class AbstractJoinGameServerHandler implements JoinGameServerHandler
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final MBassador <Event> eventBus;
  private final Set <PlayerPacket> players = new HashSet <> ();
  @Nullable
  private String selfPlayerName = null;
  @Nullable
  private JoinGameServerListener listener = null;
  @Nullable
  private GameServerConfiguration gameServerConfig = null;
  private ClientConfiguration clientConfig = new UnknownClientConfiguration ();
  private boolean isJoinGameIsInProgress = false;

  public AbstractJoinGameServerHandler (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public final void join (final String playerName, final String serverAddress, final JoinGameServerListener listener)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNull (listener, "listener");

    selfPlayerName = playerName;
    this.listener = listener;

    players.clear ();
    eventBus.subscribe (this);
    isJoinGameIsInProgress = true;

    listener.onJoinStart (playerName, new DefaultServerConfiguration (serverAddress, NetworkSettings.DEFAULT_TCP_PORT));
  }

  protected final void publishAsync (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    eventBus.publishAsync (event);
  }

  protected final void publishSync (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    eventBus.publish (event);
  }

  protected final boolean isJoinGameIsInProgress ()
  {
    return isJoinGameIsInProgress;
  }

  protected final JoinGameServerListener getListener ()
  {
    assert listener != null;
    return listener;
  }

  protected final String getSelfPlayerName ()
  {
    assert selfPlayerName != null;
    return selfPlayerName;
  }

  protected final void shutDown ()
  {
    eventBus.unsubscribe (this);
    isJoinGameIsInProgress = false;
  }

  protected abstract boolean isSelf (final JoinGameServerSuccessEvent event, final String selfPlayerName);

  protected abstract boolean isSelf (final JoinGameServerDeniedEvent event, final String selfPlayerName);

  protected abstract boolean isSelf (final PlayerJoinGameSuccessEvent event, final String selfPlayerName);

  protected abstract boolean isSelf (final PlayerJoinGameDeniedEvent event, final String selfPlayerName);

  @Handler
  final void onEvent (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (isJoinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    assert selfPlayerName != null;
    if (!isSelf (event, selfPlayerName)) return;

    log.trace ("Event received [{}]", event);
    log.info ("Client: [{}] successfully joined game server: [{}]", event.getClientConfiguration (), event);

    gameServerConfig = event.getGameServerConfiguration ();
    clientConfig = event.getClientConfiguration ();

    assert listener != null;
    listener.onJoinGameServerSuccess (selfPlayerName, event);
  }

  @Handler
  final void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (isJoinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    log.trace ("Event received [{}]", event);

    players.add (event.getPerson ());

    assert selfPlayerName != null;

    if (!isSelf (event, selfPlayerName))
    {
      log.debug ("Collected non-self player [{}].", event.getPerson ());
      return;
    }

    log.info ("Successfully joined game as a player: [{}]", event);

    eventBus.unsubscribe (this);

    isJoinGameIsInProgress = false;

    assert listener != null;
    players.addAll (event.getPlayersInGame ());
    listener.onJoinFinish (gameServerConfig, clientConfig, ImmutableSet.copyOf (players), event);
  }

  @Handler
  final void onEvent (final JoinGameServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (isJoinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    assert selfPlayerName != null;
    if (!isSelf (event, selfPlayerName)) return;

    log.trace ("Event received [{}]", event);
    log.error ("Could not join game server: [{}]", event);

    eventBus.unsubscribe (this);

    isJoinGameIsInProgress = false;

    assert listener != null;
    listener.onJoinGameServerFailure (selfPlayerName, event);
  }

  @Handler
  final void onEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (isJoinGameIsInProgress,
                               Strings.format ("{}#join has not been called first.",
                                               JoinGameServerHandler.class.getSimpleName ()));

    assert selfPlayerName != null;
    if (!isSelf (event, selfPlayerName)) return;

    log.trace ("Event received [{}]", event);
    log.error ("Could not join game as a player: [{}]", event);

    eventBus.unsubscribe (this);

    isJoinGameIsInProgress = false;

    assert listener != null;
    assert event.getPlayerName ().equals (selfPlayerName);
    listener.onPlayerJoinGameFailure (event);
  }
}
