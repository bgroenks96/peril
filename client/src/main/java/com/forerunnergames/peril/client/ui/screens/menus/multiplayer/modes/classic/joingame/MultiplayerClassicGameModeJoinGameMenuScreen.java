package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
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
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.loading.JoinGameServerHandler;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListenerAdapter;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.NetworkConstants;

import net.engio.mbassy.bus.MBassador;

public final class MultiplayerClassicGameModeJoinGameMenuScreen extends AbstractMenuScreen
{
  private final Popup errorPopup;
  private final TextField playerNameTextField;
  private final TextField clanNameTextField;
  private final TextField serverAddressTextField;
  private final CheckBox clanNameCheckBox;

  public MultiplayerClassicGameModeJoinGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                                       final ScreenChanger screenChanger,
                                                       final ScreenSize screenSize,
                                                       final Cursor normalCursor,
                                                       final Batch batch,
                                                       final JoinGameServerHandler joinGameServerHandler,
                                                       final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, normalCursor, batch);

    Arguments.checkIsNotNull (joinGameServerHandler, "joinGameHandler");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    errorPopup = createErrorPopup (new PopupListenerAdapter ());

    addTitle ("JOIN MULTIPLAYER GAME", Align.bottomLeft, 40);
    addSubTitle ("CLASSIC MODE", Align.topLeft, 40);

    playerNameTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_NAME_LENGTH,
                                                         InputSettings.VALID_PLAYER_NAME_TEXTFIELD_INPUT_PATTERN);

    clanNameTextField = widgetFactory.createTextField (GameSettings.MAX_CLAN_NAME_LENGTH,
                                                       InputSettings.VALID_CLAN_NAME_TEXTFIELD_PATTERN);

    serverAddressTextField = widgetFactory.createTextField (NetworkConstants.MAX_SERVER_ADDRESS_STRING_LENGTH,
                                                            NetworkConstants.SERVER_ADDRESS_PATTERN);

    clanNameCheckBox = widgetFactory.createCheckBox ();
    clanNameCheckBox.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        clanNameTextField.setText ("");
        clanNameTextField.setDisabled (!clanNameCheckBox.isChecked ());
      }
    });

    clanNameCheckBox.setChecked (false);
    clanNameTextField.setDisabled (true);

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
    playerSettingsTable.add (clanNameCheckBox).size (18, 18).fill ().left ().spaceLeft (10).spaceRight (10);
    playerSettingsTable.add (clanNameTextField).size (74, 28).fill ().left ().spaceLeft (10);
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
        final String playerName = playerNameTextField.getText ();

        if (!GameSettings.isValidPlayerNameWithoutClanTag (playerName))
        {
          errorPopup.setMessage (new DefaultMessage (
                  Strings.format ("Invalid player name: \'{}\'\n\nValid player name rules:\n\n{}", playerName,
                                  GameSettings.VALID_PLAYER_NAME_DESCRIPTION)));
          errorPopup.show ();
          return;
        }

        final String clanName = clanNameTextField.getText ();

        if (!clanNameTextField.isDisabled () && !GameSettings.isValidClanName (clanName))
        {
          errorPopup.setMessage (new DefaultMessage (
                  Strings.format ("Invalid clan tag: \'{}\'\n\nValid clan tag rules:\n\n{}", clanName,
                                  GameSettings.VALID_CLAN_NAME_DESCRIPTION)));
          errorPopup.show ();
          return;
        }

        final String playerNameWithOptionalClanTag = GameSettings.getPlayerNameWithOptionalClanTag (playerName, clanName);
        final String serverAddress = serverAddressTextField.getText ();

        if (!NetworkConstants.isValidAddress (serverAddress))
        {
          errorPopup.setMessage (new DefaultMessage (
                  Strings.format ("Invalid server address: \'{}\'\n\nValid server address rules:\n\n{}",
                          serverAddress, NetworkSettings.VALID_SERVER_ADDRESS_DESCRIPTION)));
          errorPopup.show ();
          return;
        }

        toScreen (ScreenId.LOADING);

        eventBus.publish (new JoinGameEvent (playerNameWithOptionalClanTag, serverAddress));
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
