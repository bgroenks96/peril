package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.core.model.settings.GameSettings;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class MultiplayerClassicGameModeJoinGameMenuScreen extends AbstractMenuScreen
{
  private static final int COUNTRY_COUNT = 49;
  private final MBassador <Event> eventBus;
  private final TextField playerNameTextField;
  private final TextField playerClanTagTextField;
  private final TextField serverAddressTextField;
  private final CheckBox playerClanTagCheckBox;

  public MultiplayerClassicGameModeJoinGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                                       final ScreenChanger screenChanger,
                                                       final ScreenSize screenSize,
                                                       final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize);

    this.eventBus = eventBus;

    addTitle ("JOIN MULTIPLAYER GAME", Align.bottomLeft, 40);
    addTitle ("CLASSIC MODE", Align.topLeft, 40);

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
    playerSettingsTable.add (widgetFactory.createLabel ("Your Player", Align.left)).size (538, 42).fill ().padLeft (60)
            .padRight (60).left ().colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createLabel ("Name", Align.left)).size (150, 40).fill ().padLeft (90)
            .left ().spaceRight (10);
    playerSettingsTable.add (playerNameTextField).size (204, 28).fill ().left ().colspan (3).spaceLeft (10);
    playerSettingsTable.add ().expandX ().fill ();
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createLabel ("Clan Tag", Align.left)).size (150, 40).fill ().padLeft (90)
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
    gameSettingsTable.add (widgetFactory.createLabel ("Game Settings", Align.left)).size (538, 42).fill ().padLeft (60)
            .padRight (60).left ().colspan (3);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createLabel ("Server Address", Align.left)).size (150, 40).fill ()
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
        //        final String rawPlayerName = playerNameTextField.getText ();
        //        final String rawPlayerClanTag = playerClanTagTextField.getText ();
        //        final String finalPlayerName = rawPlayerClanTag.isEmpty () ? rawPlayerName
        //                : GameSettings.PLAYER_CLAN_TAG_START_SYMBOL + rawPlayerClanTag
        //                        + GameSettings.PLAYER_CLAN_TAG_END_SYMBOL + " " + rawPlayerName;
        //        final String finalServerAddress = serverAddressTextField.getText ();

        // TODO Go to loading screen, which will listen for a JoinGameServerRequestEvent.
        toScreen (ScreenId.PLAY_CLASSIC);

        //        Gdx.app.postRunnable (new Runnable ()
        //        {
        //          @Override
        //          public void run ()
        //          {
        //            // TODO Production: Remove.
        //            eventBus.publish (new DefaultStatusMessageEvent (new DefaultStatusMessage (finalPlayerName + " joined "
        //                    + finalServerAddress + ".")));
        //
        //            // TODO Replace GameServerConfiguration values with real values.
        //            // TODO Uncomment.
        //            eventBus.publish (new JoinGameServerRequestEvent (new DefaultServerConfiguration ("", finalServerAddress,
        //                    NetworkSettings.DEFAULT_TCP_PORT)));
        //          }
        //        });
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
