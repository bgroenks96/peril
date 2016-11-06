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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.events.JoinGameEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import net.engio.mbassy.bus.MBassador;

public final class MultiplayerClassicGameModeJoinGameMenuScreen extends AbstractMenuScreen
{
  private final MenuScreenWidgetFactory widgetFactory;
  private final Dialog errorDialog;
  private final TextField playerNameTextField;
  private final TextField clanAcronymTextField;
  private final TextField serverAddressTextField;
  private final CheckBox clanAcronymCheckBox;
  private final Label playerSettingsSectionTitleLabel;
  private final Label playerNameSettingLabel;
  private final Label clanTagSettingLabel;
  private final Label gameSettingsSectionTitleLabel;
  private final Label serverAddressSettingLabel;
  private final Button forwardButton;
  private boolean isFirstTimeOnScreen = true;

  public MultiplayerClassicGameModeJoinGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                                       final ScreenChanger screenChanger,
                                                       final ScreenSize screenSize,
                                                       final MouseInput mouseInput,
                                                       final Batch batch,
                                                       final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;

    errorDialog = createErrorDialog ();

    addTitle ("JOIN MULTIPLAYER GAME", Align.bottomLeft, 40);
    addSubTitle ("CLASSIC MODE");

    playerNameTextField = widgetFactory.createPlayerNameTextField ();
    clanAcronymTextField = widgetFactory.createClanAcronymTextField ();
    serverAddressTextField = widgetFactory.createServerAddressTextField ();

    clanAcronymCheckBox = widgetFactory.createClanAcronymCheckBox (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        clanAcronymTextField.setText (clanAcronymCheckBox.isChecked () ? clanAcronymTextField.getText () : "");
        clanAcronymTextField.setDisabled (!clanAcronymCheckBox.isChecked ());
      }
    });

    clanAcronymCheckBox.setChecked (!clanAcronymTextField.getText ().isEmpty ());
    clanAcronymTextField.setDisabled (!clanAcronymCheckBox.isChecked ());

    playerSettingsSectionTitleLabel = widgetFactory.createPlayerSettingsSectionTitleLabel ();
    playerNameSettingLabel = widgetFactory.createPlayerNameSettingLabel ();
    clanTagSettingLabel = widgetFactory.createClanTagSettingLabel ();
    gameSettingsSectionTitleLabel = widgetFactory.createGameSettingsSectionTitleLabel ();
    serverAddressSettingLabel = widgetFactory.createMenuSettingLabel ("Server Address");

    final VerticalGroup verticalGroup = new VerticalGroup ();
    verticalGroup.align (Align.topLeft);

    // @formatter:off
    final Table playerSettingsTable = new Table ().top ().left ();
    playerSettingsTable.add ().height (23).colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (playerSettingsSectionTitleLabel).size (538, 42).fill ().padLeft (60).padRight (60).left ().colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (playerNameSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    playerSettingsTable.add (playerNameTextField).size (204, 28).fill ().left ().colspan (3).spaceLeft (10);
    playerSettingsTable.add ().expandX ().fill ();
    playerSettingsTable.row ();
    playerSettingsTable.add (clanTagSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    playerSettingsTable.add (clanAcronymCheckBox).size (18, 18).fill ().left ().spaceLeft (10).spaceRight (10);
    playerSettingsTable.add (clanAcronymTextField).size (74, 28).fill ().left ().spaceLeft (10);
    playerSettingsTable.add ().width (102).fill ();
    playerSettingsTable.add ().expandX ().fill ();
    verticalGroup.addActor (playerSettingsTable);
    // @formatter:on

    // @formatter:off
    final Table gameSettingsTable = new Table ().top ().left ();
    gameSettingsTable.row ();
    gameSettingsTable.add ().height (18).colspan (3);
    gameSettingsTable.row ();
    gameSettingsTable.add (gameSettingsSectionTitleLabel).size (538, 42).fill ().padLeft (60).padRight (60).left ().colspan (3);
    gameSettingsTable.row ();
    gameSettingsTable.add (serverAddressSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (serverAddressTextField).size (204, 28).fill ().left ().spaceLeft (10);
    gameSettingsTable.add ().expandX ().fill ();
    verticalGroup.addActor (gameSettingsTable);
    // @formatter:on

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

    forwardButton = addForwardButton ("JOIN GAME", new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        final String playerName = playerNameTextField.getText ();

        if (!GameSettings.isValidPlayerNameWithoutClanTag (playerName))
        {
          errorDialog.setMessage (new DefaultMessage (Strings
                  .format ("Invalid player name: \'{}\'\n\nValid player name rules:\n\n{}", playerName,
                           GameSettings.VALID_PLAYER_NAME_DESCRIPTION)));
          errorDialog.show ();
          return;
        }

        final String clanAcronym = clanAcronymTextField.getText ();

        if (!clanAcronymTextField.isDisabled () && !GameSettings.isValidHumanClanAcronym (clanAcronym))
        {
          errorDialog.setMessage (new DefaultMessage (Strings
                  .format ("Invalid clan tag: \'{}\'\n\nValid clan tag rules:\n\n{}", clanAcronym,
                           GameSettings.VALID_CLAN_ACRONYM_DESCRIPTION)));
          errorDialog.show ();
          return;
        }

        final String playerNameWithOptionalClanTag = GameSettings.getHumanPlayerNameWithOptionalClanTag (playerName,
                                                                                                    clanAcronym);
        final String serverAddress = serverAddressTextField.getText ();

        if (!NetworkSettings.isValidServerAddress (serverAddress))
        {
          errorDialog.setMessage (new DefaultMessage (Strings
                  .format ("Invalid server address: \'{}\'\n\nValid server address rules:\n\n{}", serverAddress,
                           NetworkSettings.VALID_SERVER_ADDRESS_DESCRIPTION)));
          errorDialog.show ();
          return;
        }

        toScreen (ScreenId.MENU_TO_PLAY_LOADING);

        // The menu-to-play loading screen is now active & can therefore receive events.

        publishAsync (new JoinGameEvent (playerNameWithOptionalClanTag, serverAddress));
      }
    });
  }

  @Override
  public void show ()
  {
    super.show ();

    expandMenuBar ();

    playerNameTextField.setStyle (widgetFactory.createPlayerNameTextFieldStyle ());
    clanAcronymTextField.setStyle (widgetFactory.createClanAcronymTextFieldStyle ());
    serverAddressTextField.setStyle (widgetFactory.createServerAddressTextFieldStyle ());
    clanAcronymCheckBox.setStyle (widgetFactory.createClanAcronymCheckBoxStyle ());
    playerSettingsSectionTitleLabel.setStyle (widgetFactory.createMenuSettingSectionTitleLabelStyle ());
    playerNameSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    clanTagSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    gameSettingsSectionTitleLabel.setStyle (widgetFactory.createGameSettingsSectionTitleLabelStyle ());
    serverAddressSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());

    if (isFirstTimeOnScreen && InputSettings.AUTO_JOIN_MULTIPLAYER_GAME)
    {
      // Execute next frame because a screen transition is still in progress.
      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          forwardButton.toggle ();
        }
      });

      isFirstTimeOnScreen = false;
    }
  }

  @Override
  protected boolean onEscape ()
  {
    if (!super.onEscape ())
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
    return true;
  }
}
