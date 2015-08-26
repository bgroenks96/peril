package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

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
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.loading.JoinGameServerHandler;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.NetworkConstants;

import net.engio.mbassy.bus.MBassador;

public final class MultiplayerClassicGameModeJoinGameMenuScreen extends AbstractMenuScreen
{
  private final TextField playerNameTextField;
  private final TextField playerClanTagTextField;
  private final TextField serverAddressTextField;
  private final CheckBox playerClanTagCheckBox;

  public MultiplayerClassicGameModeJoinGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                                       final ScreenChanger screenChanger,
                                                       final ScreenSize screenSize,
                                                       final Batch batch,
                                                       final JoinGameServerHandler joinGameServerHandler,
                                                       final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, batch);

    Arguments.checkIsNotNull (joinGameServerHandler, "joinGameHandler");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    addTitle ("JOIN MULTIPLAYER GAME", Align.bottomLeft, 40);
    addSubTitle ("CLASSIC MODE", Align.topLeft, 40);

    playerNameTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_NAME_LENGTH,
                                                         GameSettings.PLAYER_NAME_PATTERN);

    playerClanTagTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_CLAN_TAG_LENGTH,
                                                            GameSettings.PLAYER_CLAN_TAG_PATTERN);

    serverAddressTextField = widgetFactory.createTextField (NetworkConstants.MAX_SERVER_ADDRESS_STRING_LENGTH,
                                                            NetworkConstants.SERVER_ADDRESS_PATTERN);

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
    playerSettingsTable.add (widgetFactory.createMenuSettingSectionTitleText ("Your Player")).size (538, 42).fill ()
            .padLeft (60).padRight (60).left ().colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createMenuSettingText ("Name")).size (150, 40).fill ().padLeft (90).left ()
            .spaceRight (10);
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
    gameSettingsTable.add (widgetFactory.createMenuSettingSectionTitleText ("Game Settings")).size (538, 42).fill ()
            .padLeft (60).padRight (60).left ().colspan (3);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Server Address")).size (150, 40).fill ().padLeft (90)
            .left ().spaceRight (10);
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
                : GameSettings.PLAYER_CLAN_TAG_START_SYMBOL + rawPlayerClanTag + GameSettings.PLAYER_CLAN_TAG_END_SYMBOL
                        + " " + rawPlayerName;
        final String finalServerAddress = serverAddressTextField.getText ();

        toScreen (ScreenId.LOADING);

        eventBus.publish (new JoinGameEvent (finalPlayerName, finalServerAddress));
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
}
