package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame;

import com.forerunnergames.peril.client.events.CreateGameDeniedEvent;
import com.forerunnergames.peril.client.events.CreateGameRequestEvent;
import com.forerunnergames.peril.client.events.CreateGameSuccessEvent;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.shared.JoinGameHandler;
import com.forerunnergames.peril.core.shared.game.GameConfiguration;
import com.forerunnergames.peril.core.shared.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.net.NetworkConstants;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener (references = References.Strong)
public final class DefaultCreateGameHandler implements CreateGameHandler
{
  private static final Logger log = LoggerFactory.getLogger (DefaultCreateGameHandler.class);
  private final JoinGameHandler joinGameHandler;
  private final MBassador <Event> eventBus;
  @Nullable
  private String playerName = null;
  private boolean createGameIsInProgress = false;

  public DefaultCreateGameHandler (final JoinGameHandler joinGameHandler, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (joinGameHandler, "joinGameHandler");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.joinGameHandler = joinGameHandler;
    this.eventBus = eventBus;
  }

  @Override
  public void createGame (final String serverName, final GameConfiguration gameConfig, final String playerName)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (playerName, "playerName");

    this.playerName = playerName;

    // TODO Go to loading screen

    eventBus.subscribe (this);

    final CreateGameRequestEvent event = new CreateGameRequestEvent (new DefaultGameServerConfiguration (serverName,
            GameServerType.HOST_AND_PLAY, gameConfig,
            new DefaultServerConfiguration (NetworkConstants.LOCALHOST_ADDRESS, NetworkSettings.DEFAULT_TCP_PORT)));

    log.info ("Attempting to create game... [{}]", event);

    eventBus.publishAsync (event);

    createGameIsInProgress = true;
  }

  @Handler
  void onEvent (final CreateGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (createGameIsInProgress, "CreateGameHandler#createGame has not been called first.");

    log.trace ("Event received [{}].", event);
    log.info ("Successfully created game: [{}]", event);

    // TODO Loading screen progress update

    // Attempt to join the created game.
    // When JoinGameHandler#joinGame returns, it will be subscribed
    joinGameHandler.joinGame (playerName, NetworkConstants.LOCALHOST_ADDRESS);

    // Don't unsubscribe until we're already subscribed in the JoinGameHandler.
    eventBus.unsubscribe (this);

    createGameIsInProgress = false;
  }

  @Handler
  void onEvent (final CreateGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (createGameIsInProgress, "CreateGameHandler#createGame has not been called first.");

    log.trace ("Event received [{}].", event);
    log.error ("Could not create game: [{}]", event);

    eventBus.unsubscribe (this);

    // TODO Error popup
    // TODO Go back to this screen from the loading screen on error popup confirmation.

    createGameIsInProgress = false;
  }
}
