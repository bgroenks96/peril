package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.shared;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.clientConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.gameServerConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playersInGameFrom;

import com.forerunnergames.peril.client.events.JoinGameSuccessEvent;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.core.shared.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.client.UnknownClientConfiguration;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener (references = References.Strong)
public class DefaultJoinGameHandler implements JoinGameHandler
{
  private static final Logger log = LoggerFactory.getLogger (DefaultJoinGameHandler.class);
  private final ScreenChanger screenChanger;
  private final MBassador <Event> eventBus;
  private final ImmutableSet.Builder <PlayerPacket> playersBuilder = ImmutableSet.builder ();
  @Nullable
  private String playerName = null;
  @Nullable
  private GameServerConfiguration gameServerConfig = null;
  private ClientConfiguration clientConfig = new UnknownClientConfiguration ();
  private boolean joinGameIsInProgress = false;

  public DefaultJoinGameHandler (final ScreenChanger screenChanger, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenChanger = screenChanger;
    this.eventBus = eventBus;
  }

  @Override
  public void joinGame (final String playerName, final String serverAddress)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");

    this.playerName = playerName;

    // TODO Go to loading screen

    eventBus.subscribe (this);

    final JoinGameServerRequestEvent event = new JoinGameServerRequestEvent (
            new DefaultServerConfiguration (serverAddress, NetworkSettings.DEFAULT_TCP_PORT));

    log.info ("Attempting to join game server... [{}]", event);

    eventBus.publishAsync (event);

    joinGameIsInProgress = true;
  }

  @Handler
  void onJoinGameServerSuccessEvent (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress, "JoinGameHandler#joinGame has not been called first.");

    log.trace ("Event received [{}]", event);
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
  void onJoinGameServerDeniedEvent (final JoinGameServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress, "JoinGameHandler#joinGame has not been called first.");

    log.trace ("Event received [{}]", event);
    log.error ("Could not join game server: [{}]", event);

    // TODO Error popup
    // TODO Go back to this screen from the loading screen on error popup confirmation.

    eventBus.unsubscribe (this);

    joinGameIsInProgress = false;
  }

  @Handler
  void onPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress, "JoinGameHandler#joinGame has not been called first.");

    log.trace ("Event received [{}]", event);

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

    // Don't unsubscribe until we're already subscribed on the play screen.
    eventBus.unsubscribe (this);

    // Forward the data from JoinGameServerSuccessEvent & PlayerJoinGameSuccessEvent to the play screen.
    eventBus.publishAsync (new JoinGameSuccessEvent (playersBuilder.build (), gameServerConfig, clientConfig));

    joinGameIsInProgress = false;
  }

  @Handler
  void onPlayerJoinGameDeniedEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (joinGameIsInProgress, "JoinGameHandler#joinGame has not been called first.");

    log.trace ("Event received [{}]", event);

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

    joinGameIsInProgress = false;
  }
}
