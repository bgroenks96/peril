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

package com.forerunnergames.peril.client.ui.screens.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.events.ConnectToServerRequestEvent;
import com.forerunnergames.peril.client.events.CreateGameEvent;
import com.forerunnergames.peril.client.events.JoinGameEvent;
import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.UnloadPlayMapRequestEvent;
import com.forerunnergames.peril.client.events.UnloadPlayScreenAssetsRequestEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMapFactory;
import com.forerunnergames.peril.client.ui.screens.menus.modes.classic.creategame.CreateGameServerHandler;
import com.forerunnergames.peril.client.ui.screens.menus.modes.classic.creategame.CreateGameServerListener;
import com.forerunnergames.peril.client.ui.screens.menus.modes.classic.creategame.DefaultCreateGameServerHandler;
import com.forerunnergames.peril.client.ui.screens.menus.modes.classic.joingame.HumanJoinGameServerHandler;
import com.forerunnergames.peril.common.JoinGameServerHandler;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.PublicationEvent;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MenuToPlayLoadingScreen extends AbstractLoadingScreen
{
  private static final Logger log = LoggerFactory.getLogger (MenuToPlayLoadingScreen.class);
  private static final String QUIT_DIALOG_MESSAGE = "Are you sure you want to quit the current game?";
  private final ResetProgressListener goToPlayToMenuLoadingScreenListener = new GoToPlayToMenuLoadingScreenListener ();
  private final Collection <ServerEvent> unhandledServerEvents = new ArrayList <> ();
  private final Set <PlayerPacket> players = new HashSet <> ();
  private final PlayMapFactory playMapFactory;
  private final JoinGameServerHandler joinGameServerHandler;
  private final CreateGameServerHandler createGameServerHandler;
  private final CreateGameServerListener createGameServerListener;
  private PlayMapMetadata playMapMetadata = PlayMapMetadata.NULL;
  private boolean createdGameFirst;
  private boolean isLoadingPlayMapAssets;
  @Nullable
  private GameServerConfiguration gameServerConfiguration;
  @Nullable
  private ClientConfiguration clientConfiguration;
  @Nullable
  private PlayerPacket selfPlayer;

  public MenuToPlayLoadingScreen (final LoadingScreenWidgetFactory widgetFactory,
                                  final ScreenChanger screenChanger,
                                  final ScreenSize screenSize,
                                  final MouseInput mouseInput,
                                  final Batch batch,
                                  final AssetManager assetManager,
                                  final MBassador <Event> eventBus,
                                  final PlayMapFactory playMapFactory)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus, assetManager, LoadingScreenStyle
            .builder ().quitDialogMessageText (QUIT_DIALOG_MESSAGE).build ());

    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (playMapFactory, "playMapFactory");

    this.playMapFactory = playMapFactory;

    joinGameServerHandler = new HumanJoinGameServerHandler (eventBus);
    createGameServerHandler = new DefaultCreateGameServerHandler (joinGameServerHandler, eventBus);
    createGameServerListener = new DefaultCreateGameServerListener ();
  }

  @Override
  public void hide ()
  {
    super.hide ();

    playMapMetadata = PlayMapMetadata.NULL;
    gameServerConfiguration = null;
    clientConfiguration = null;
    createdGameFirst = false;
    isLoadingPlayMapAssets = false;
    selfPlayer = null;
    unhandledServerEvents.clear ();
    players.clear ();
  }

  @Override
  protected void onProgressFinished ()
  {
    status ("Unloading menu assets...");
    unloadAssetsSync (AssetSettings.MENU_SCREEN_ASSET_DESCRIPTORS);
    status ("Ready!");
    resetProgress (new GoToPlayScreenListener (createPlayMap ()));
  }

  @Override
  protected void onQuitDialogSubmit ()
  {
    publishAsync (new QuitGameEvent ());
    resetProgress (goToPlayToMenuLoadingScreenListener);
  }

  @Override
  protected void onErrorDialogSubmit ()
  {
    resetProgress (goToPlayToMenuLoadingScreenListener);
  }

  @Override
  protected void onAssetLoadingFinished ()
  {
    if (!isLoadingPlayMapAssets)
    {
      loadPlayMapAssetsAsync ();
      return;
    }

    isLoadingPlayMapAssets = !isFinishedLoadingPlayMapAssets ();
  }

  @Override
  protected void onErrorDialogShow ()
  {
    publishAsync (new QuitGameEvent ());
  }

  @Override
  protected float normalize (final float assetLoadingProgressIncrease)
  {
    return assetLoadingProgressIncrease * (createdGameFirst ? ONE_SIXTH : ONE_FOURTH);
  }

  @Handler
  void onEvent (final CreateGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        createdGameFirst = true;
        createGameServerHandler.create (event.getServerName (), event.getGameConfiguration (), event.getPlayerName (),
                                        createGameServerListener);
      }
    });
  }

  @Handler
  void onEvent (final JoinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        createdGameFirst = false;
        joinGameServerHandler.join (event.getPlayerName (), event.getServerAddress (), createGameServerListener);
      }
    });
  }

  // After having joined the game, server events start arriving intended for the play screen,
  // while the play map is still loading on this screen (play screen not active yet - cannot receive events).
  // Collect unhandled server events, which will be published after the play screen is active.
  @Handler
  void onEvent (final PublicationEvent unhandledEvent)
  {
    Arguments.checkIsNotNull (unhandledEvent, "unhandledEvent");

    final Object message = unhandledEvent.getMessage ();

    if (!(message instanceof ServerEvent))
    {
      log.warn ("Not collecting unhandled event for play screen: [{}]", message);
      return;
    }

    final ServerEvent event = (ServerEvent) message;

    log.debug ("Collecting unhandled event for play screen: [{}]", event);

    unhandledServerEvents.add (event);
  }

  private void loadPlayMapAssetsAsync ()
  {
    isLoadingPlayMapAssets = true;

    statusWithProgressPercent ("Loading map \"{}\"...", playMapMetadata.getName ());

    loadAssetsAsync (new Runnable ()
    {
      @Override
      public void run ()
      {
        try
        {
          playMapFactory.loadAssets (playMapMetadata);
        }
        catch (final PlayMapLoadingException e)
        {
          handlePlayMapLoadingException (e);
        }
      }
    });
  }

  private PlayMap createPlayMap ()
  {
    assert isFinishedLoadingPlayMapAssets ();

    try
    {
      return playMapFactory.create (playMapMetadata);
    }
    catch (final PlayMapLoadingException e)
    {
      handlePlayMapLoadingException (e);
      log.warn ("Could not create {}: [{}]. Returning [{}] instead.", PlayMap.class.getSimpleName (), playMapMetadata,
                PlayMap.NULL.getClass ().getSimpleName ());
      return PlayMap.NULL;
    }
  }

  private boolean isFinishedLoadingPlayMapAssets ()
  {
    return playMapFactory.isFinishedLoadingAssets (playMapMetadata);
  }

  private void handlePlayMapLoadingException (final PlayMapLoadingException e)
  {
    assert playMapMetadata != null;

    handleError ("A crash file has been created in \"{}\".\n\nThere was a problem loading resources for {} map \'{}\'."
                         + "\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                 CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                 playMapMetadata.getType ().name ().toLowerCase (), playMapMetadata.getName (), Throwables
                         .getRootCause (e).getMessage (), Strings.toString (e));
  }

  private final class DefaultCreateGameServerListener implements CreateGameServerListener
  {
    @Override
    public void onCreateStart (final String playerName, final GameServerConfiguration config)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (config, "config");

      log.trace ("onCreateStart: {} [{}], Player Name [{}]", GameServerConfiguration.class.getSimpleName (), config,
                 playerName);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          resetProgress ();
          statusWithProgressPercent ("Creating server...");
        }
      });
    }

    @Override
    public void onCreateFinish (final GameServerConfiguration config)
    {
      Arguments.checkIsNotNull (config, "config");

      log.trace ("onCreateFinish: {} [{}]", GameServerConfiguration.class.getSimpleName (), config);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          increaseProgressBy (ONE_THIRD);
        }
      });
    }

    @Override
    public void onCreateFailure (final GameServerConfiguration config, final String reason)
    {
      Arguments.checkIsNotNull (config, "config");
      Arguments.checkIsNotNull (reason, "reason");

      log.trace ("onCreateFailure: {} [{}], Reason [{}]", GameServerConfiguration.class.getSimpleName (), config,
                 reason);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          handleError ("{}", reason);
        }
      });
    }

    @Override
    public void onJoinStart (final String playerName, final ServerConfiguration config)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (config, "config");

      log.trace ("onJoinStart: Player Name [{}], {} [{}] ", playerName, ServerConfiguration.class.getSimpleName (),
                 config);

      publishAsync (new ConnectToServerRequestEvent (config));

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          if (!createdGameFirst) resetProgress ();
          statusWithProgressPercent ("Connecting to server...");
        }
      });
    }

    @Override
    public void onConnectToServerSuccess (final String playerName, final ServerConfiguration config)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (config, "config");

      log.trace ("onConnectToServerSuccess: {} [{}]", ServerConfiguration.class.getSimpleName (), config);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          increaseProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
          statusWithProgressPercent ("Joining game...");
        }
      });
    }

    @Override
    public void onJoinGameServerSuccess (final String playerName, final JoinGameServerSuccessEvent event)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (event, "event");

      log.trace ("onJoinGameServerSuccess: PlayerName: [{}], Event: [{}]", playerName, event);

      publishAsync (new HumanPlayerJoinGameRequestEvent (playerName));

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          increaseProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
        }
      });
    }

    @Override
    public void onConnectToServerFailure (final String playerName, final ServerConfiguration config, final String reason)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (config, "config");
      Arguments.checkIsNotNull (reason, "reason");

      log.trace ("onConnectToServerFailure: {} [{}], Reason [{}]", ServerConfiguration.class.getSimpleName (), config,
                 reason);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          handleError ("{}", reason);
        }
      });
    }

    @Override
    public void onJoinGameServerFailure (final String playerName, final JoinGameServerDeniedEvent event)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (event, "event");

      log.trace ("onJoinGameServerFailure: Event: [{}].", event);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          handleError ("{}", event.getReason ());
        }
      });
    }

    @Override
    public void onPlayerJoinGameFailure (final PlayerJoinGameDeniedEvent event)
    {
      Arguments.checkIsNotNull (event, "event");

      log.trace ("onPlayerJoinGameFailure: Event: [{}]", event);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          handleError ("{}", asText (event.getReason (), event.getPlayerName ()));
        }
      });
    }

    @Override
    public void onJoinFinish (final GameServerConfiguration gameServerConfig,
                              final ClientConfiguration clientConfig,
                              final ImmutableSet <PlayerPacket> players,
                              final PlayerJoinGameSuccessEvent event)
    {
      Arguments.checkIsNotNull (gameServerConfig, "gameServerConfiguration");
      Arguments.checkIsNotNull (clientConfig, "clientConfiguration");
      Arguments.checkIsNotNull (players, "players");
      Arguments.checkHasNoNullElements (players, "players");
      Arguments.checkIsNotNull (event, "event");

      log.trace ("onJoinFinish: {} [{}], {} [{}], Players [{}]", GameServerConfiguration.class.getSimpleName (),
                 gameServerConfig, ClientConfiguration.class.getSimpleName (), clientConfig, players);

      assert event.hasIdentity (PersonIdentity.SELF);

      selfPlayer = event.getPerson ();

      gameServerConfiguration = gameServerConfig;
      playMapMetadata = gameServerConfig.getPlayMapMetadata ();
      clientConfiguration = clientConfig;
      MenuToPlayLoadingScreen.this.players.addAll (players);

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          increaseProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
          loadPlayScreenAssetsAsync (gameServerConfig.getGameMode ());
        }
      });
    }

    private void loadPlayScreenAssetsAsync (final GameMode gameMode)
    {
      statusWithProgressPercent ("Loading assets...");
      loadAssetsAsync (getPlayScreenAssetDescriptorsForGameMode (gameMode));
    }

    private ImmutableList <AssetDescriptor <?>> getPlayScreenAssetDescriptorsForGameMode (final GameMode mode)
    {
      return AssetSettings.fromGameMode (mode);
    }

    private String asText (final PlayerJoinGameDeniedEvent.Reason reason, final String playerName)
    {
      switch (reason)
      {
        case GAME_IS_FULL:
        {
          return "This game is already full.";
        }
        case DUPLICATE_NAME:
        {
          return "Your name, " + playerName + ", is already taken by another player.";
        }
        case DUPLICATE_COLOR:
        {
          return "Your color is already taken by another player.";
        }
        case DUPLICATE_TURN_ORDER:
        {
          return "Your turn order is already taken by another player.";
        }
        case INVALID_NAME:
        {
          return "Your player name is invalid.";
        }
        default:
        {
          return "Unknown";
        }
      }
    }
  }

  private final class GoToPlayScreenListener implements ResetProgressListener
  {
    private final PlayMap playMap;

    GoToPlayScreenListener (final PlayMap playMap)
    {
      Arguments.checkIsNotNull (playMap, "playMap");

      this.playMap = playMap;
    }

    @Override
    public void onResetProgressComplete ()
    {
      if (playMap.equals (PlayMap.NULL))
      {
        log.warn ("Not going to play screen. {} is [{}].", PlayMap.class.getSimpleName (), playMap.getClass ()
                .getSimpleName ());
        return;
      }

      assert gameServerConfiguration != null;
      assert clientConfiguration != null;
      assert selfPlayer != null;

      // gameServerConfiguration, clientConfiguration, selfPlayer must be used here because
      // they will be made null in #hide during the call to #toScreen.
      // unhandledServerEvents will also be cleared, so make a defensive copy.
      final PlayGameEvent playGameEvent = new PlayGameEvent (gameServerConfiguration, clientConfiguration, selfPlayer,
              ImmutableSet.copyOf (players), playMap);
      final ScreenId playScreen = ScreenId.fromGameMode (gameServerConfiguration.getGameMode ());
      final ImmutableList <ServerEvent> unhandledServerEventsCopy = ImmutableList.copyOf (unhandledServerEvents);

      toScreen (playScreen);

      // The play screen is now active & can therefore receive events.

      publish (playGameEvent);

      for (final ServerEvent event : unhandledServerEventsCopy)
      {
        publish (event);
      }
    }
  }

  private final class GoToPlayToMenuLoadingScreenListener implements ResetProgressListener
  {
    @Override
    public void onResetProgressComplete ()
    {
      // playMapMetadata & gameServerConfiguration must be used / copied here because
      // they will be made null in #hide during the call to #toScreen.
      final UnloadPlayMapRequestEvent unloadPlayMapRequestEvent = new UnloadPlayMapRequestEvent (playMapMetadata);
      @Nullable
      final GameServerConfiguration gameServerConfiguration = MenuToPlayLoadingScreen.this.gameServerConfiguration;

      toScreen (ScreenId.PLAY_TO_MENU_LOADING);

      // The play-to-menu loading screen is now active & can therefore receive events.

      publishAsync (unloadPlayMapRequestEvent);

      if (gameServerConfiguration != null)
      {
        publishAsync (new UnloadPlayScreenAssetsRequestEvent (gameServerConfiguration.getGameMode ()));
      }
    }
  }
}
