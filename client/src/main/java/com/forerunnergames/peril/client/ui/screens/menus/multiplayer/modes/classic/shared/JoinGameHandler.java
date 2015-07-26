package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.shared;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.clientConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.gameServerConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playersInGameFrom;

import com.forerunnergames.peril.client.events.JoinGameEvent;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.client.UnknownClientConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener (references = References.Strong)
public final class JoinGameHandler
{
  private static final Logger log = LoggerFactory.getLogger (JoinGameHandler.class);
  private final String playerName;
  private final ServerConfiguration serverConfig;
  private final ScreenChanger screenChanger;
  private final MBassador <Event> eventBus;
  private final ImmutableSet.Builder <PlayerPacket> playersBuilder = ImmutableSet.builder ();
  @Nullable
  private GameServerConfiguration gameServerConfig = null;
  private ClientConfiguration clientConfig = new UnknownClientConfiguration ();

  public JoinGameHandler (final String playerName,
                          final ServerConfiguration serverConfig,
                          final ScreenChanger screenChanger,
                          final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (serverConfig, "serverConfig");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerName = playerName;
    this.serverConfig = serverConfig;
    this.screenChanger = screenChanger;
    this.eventBus = eventBus;
  }

  public void joinGame ()
  {
    // TODO Go to loading screen

    eventBus.subscribe (this);

    final JoinGameServerRequestEvent event = new JoinGameServerRequestEvent (serverConfig);

    log.info ("Attempting to join game server... [{}]", event);

    eventBus.publishAsync (event);
  }

  @Handler
  public void onJoinGameServerSuccessEvent (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);
    log.info ("Successfully joined game server [{}]", event);

    // TODO Loading screen progress update

    playersBuilder.addAll (playersInGameFrom (event));
    gameServerConfig = gameServerConfigurationFrom (event);
    clientConfig = clientConfigurationFrom (event);

    final PlayerJoinGameRequestEvent playerJoinGameRequestEvent = new PlayerJoinGameRequestEvent (playerName);

    log.info ("Attempting to join game as a player... [{}]", playerJoinGameRequestEvent);

    eventBus.publishAsync (playerJoinGameRequestEvent);
  }

  @Handler
  public void onJoinGameServerDeniedEvent (final JoinGameServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);
    log.error ("Could not join game server: [{}]", event);

    // TODO Error popup
    // TODO Go back to this screen from the loading screen on error popup confirmation.

    eventBus.unsubscribe (this);
  }

  @Handler
  public void onPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    if (event.getPlayer ().has (PersonIdentity.NON_SELF))
    {
      log.warn ("Received {} with {} while expecting {}", event, PersonIdentity.NON_SELF, PersonIdentity.SELF);
      return;
    }

    playersBuilder.add (playerFrom (event));

    log.info ("Successfully joined game as a player: [{}]", event);

    // TODO Loading screen progress update

    // Go to the play screen
    // When toScreen returns, the play screen will be subscribed
    screenChanger.toScreen (ScreenId.PLAY_CLASSIC);

    // Don't unsubscribe JoinGameHandler until we're already subscribed on the play screen.
    eventBus.unsubscribe (this);

    // This should be impossible because it is set in JoinGameServerSuccessEvent handler,
    // which is a prerequisite to arriving here.
    assert gameServerConfig != null;

    // Forward the data from JoinGameServerSuccessEvent & PlayerJoinGameSuccessEvent to the play screen.
    eventBus.publishAsync (new JoinGameEvent (playersBuilder.build (), gameServerConfig, clientConfig));
  }

  @Handler
  public void onPlayerJoinGameDeniedEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    if (!playerNameFrom (event).equals (playerName))
    {
      log.warn ("Received [{}] with player name [{}] while expecting player name [{}]", event, playerNameFrom (event),
                playerName);
      return;
    }

    log.error ("Could not join game as a player: [{}]", event);

    eventBus.unsubscribe (this);

    // TODO Error popup
    // TODO Go back to this screen from the loading screen on error popup confirmation.
  }
}
