package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.clientConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.gameServerConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playersInGameFrom;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.events.JoinGameEvent;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.settings.GameSettings;
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
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.client.UnknownClientConfiguration;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiplayerClassicGameModeJoinGameMenuScreen extends AbstractMenuScreen
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerClassicGameModeJoinGameMenuScreen.class);
  private final MBassador <Event> eventBus;
  private final TextField playerNameTextField;
  private final TextField playerClanTagTextField;
  private final TextField serverAddressTextField;
  private final CheckBox playerClanTagCheckBox;

  public MultiplayerClassicGameModeJoinGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                                       final ScreenChanger screenChanger,
                                                       final ScreenSize screenSize,
                                                       final Batch batch,
                                                       final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, batch);

    this.eventBus = eventBus;

    addTitle ("JOIN MULTIPLAYER GAME", Align.bottomLeft, 40);
    addSubTitle ("CLASSIC MODE", Align.topLeft, 40);

    playerNameTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_NAME_LENGTH,
                                                         GameSettings.PLAYER_NAME_PATTERN);

    playerClanTagTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_CLAN_TAG_LENGTH,
                                                            GameSettings.PLAYER_CLAN_TAG_PATTERN);

    serverAddressTextField = widgetFactory.createTextField (NetworkSettings.MAX_SERVER_ADDRESS_LENGTH,
                                                            NetworkSettings.SERVER_ADDRESS_PATTERN);

    playerClanTagCheckBox = widgetFactory.createCheckBox ();
    playerClanTagCheckBox.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        playerClanTagTextField.setText ("");
        playerClanTagTextField.setDisabled (!playerClanTagCheckBox.isChecked ());
      }
    });

    playerClanTagCheckBox.setChecked (false);
    playerClanTagTextField.setDisabled (true);

    final VerticalGroup verticalGroup = new VerticalGroup ();
    verticalGroup.align (Align.topLeft);

    final Table playerSettingsTable = new Table ().top ().left ();
    playerSettingsTable.add ().height (23).colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createMenuSettingSectionTitleText ("Your Player")).size (538, 42).fill ().padLeft (60)
            .padRight (60).left ().colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createMenuSettingText ("Name")).size (150, 40).fill ().padLeft (90)
            .left ().spaceRight (10);
    playerSettingsTable.add (playerNameTextField).size (204, 28).fill ().left ().colspan (3).spaceLeft (10);
    playerSettingsTable.add ().expandX ().fill ();
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createMenuSettingText ("Clan Tag")).size (150, 40).fill ().padLeft (90)
            .left ().spaceRight (10);
    playerSettingsTable.add (playerClanTagCheckBox).size (18, 18).fill ().left ().spaceLeft (10).spaceRight (10);
    playerSettingsTable.add (playerClanTagTextField).size (74, 28).fill ().left ().spaceLeft (10);
    playerSettingsTable.add ().width (102).fill ();
    playerSettingsTable.add ().expandX ().fill ();
    verticalGroup.addActor (playerSettingsTable);

    final Table gameSettingsTable = new Table ().top ().left ();
    gameSettingsTable.row ();
    gameSettingsTable.add ().height (18).colspan (3);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingSectionTitleText ("Game Settings")).size (538, 42).fill ().padLeft (60)
            .padRight (60).left ().colspan (3);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Server Address")).size (150, 40).fill ()
            .padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (serverAddressTextField).size (204, 28).fill ().left ().spaceLeft (10);
    gameSettingsTable.add ().expandX ().fill ();
    verticalGroup.addActor (gameSettingsTable);

    addContent (verticalGroup);

    addBackButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        contractMenuBar (new Runnable ()
        {
          @Override
          public void run ()
          {
            toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_MENU);
          }
        });
      }
    });

    addForwardButton ("JOIN GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        final String rawPlayerName = playerNameTextField.getText ();
        final String rawPlayerClanTag = playerClanTagTextField.getText ();
        final String finalPlayerName = rawPlayerClanTag.isEmpty () ? rawPlayerName
                : GameSettings.PLAYER_CLAN_TAG_START_SYMBOL + rawPlayerClanTag
                        + GameSettings.PLAYER_CLAN_TAG_END_SYMBOL + " " + rawPlayerName;
        final String finalServerAddress = serverAddressTextField.getText ();

        // TODO Go to loading screen.

        final JoinGameHandler joinGameHandler = new JoinGameHandler (finalPlayerName, finalServerAddress,
                NetworkSettings.DEFAULT_TCP_PORT);

        joinGameHandler.joinGame ();
      }
    });
  }

  @Override
  public void show ()
  {
    super.show ();

    expandMenuBar ();
  }

  @Override
  protected void onEscape ()
  {
    contractMenuBar (new Runnable ()
    {
      @Override
      public void run ()
      {
        toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_MENU);
      }
    });
  }

  @Listener (references = References.Strong)
  private final class JoinGameHandler
  {
    private final String playerName;
    private final ServerConfiguration serverConfig;
    private final ImmutableSet.Builder <PlayerPacket> playersBuilder = ImmutableSet.builder ();
    @Nullable
    private GameServerConfiguration gameServerConfig = null;
    private ClientConfiguration clientConfig = new UnknownClientConfiguration ();

    public void joinGame ()
    {
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
      toScreen (ScreenId.PLAY_CLASSIC);

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
        log.warn ("Received [{}] with player name [{}] while expecting player name [{}]", event,
                  playerNameFrom (event), playerName);
        return;
      }

      log.error ("Could not join game as a player: [{}]", event);

      // TODO Error popup

      eventBus.unsubscribe (this);
    }

    private JoinGameHandler (final String playerName, final String serverAddress, final int tcpPort)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (serverAddress, "serverAddresss");
      Arguments.checkIsNotNegative (tcpPort, "tcpPort");

      this.playerName = playerName;
      serverConfig = new DefaultServerConfiguration (serverAddress, tcpPort);
    }
  }
}
