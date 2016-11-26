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

package com.forerunnergames.peril.client.ui.screens.menus.modes.classic.creategame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import com.forerunnergames.peril.client.events.CreateGameEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.countrycounter.CountryCounter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.ClientPlayMapMetadataLoaderFactory;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.playmap.io.PlayMapMetadataLoader;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Maths;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Iterator;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassicGameModeCreateGameMenuScreen extends AbstractMenuScreen
{
  private static final Logger log = LoggerFactory.getLogger (ClassicGameModeCreateGameMenuScreen.class);
  private static final String TITLE_TEXT = "CREATE GAME";
  private static final String SUBTITLE_TEXT = "CLASSIC MODE";
  private static final String FORWARD_BUTTON_TEXT = "CREATE GAME";
  private static final String SERVER_NAME_SETTING_LABEL_TEXT = "Title";
  private static final String HUMAN_PLAYERS_SETTING_LABEL_TEXT = "Human Players";
  private static final String AI_PLAYERS_SETTING_LABEL_TEXT = "AI Players";
  private static final String SPECTATORS_SETTING_SETTING_LABEL_TEXT = "Spectators";
  private static final String PLAY_MAP_SETTING_LABEL_TEXT = "Map";
  private static final String WIN_PERCENT_SETTING_LABEL_TEXT = "Win Percent";
  private static final String INITIAL_COUNTRY_ASSIGNMENT_SETTING_LABEL_TEXT = "Initial Countries";
  private static final PlayMapMetadataLoader PLAY_MAPS_LOADER = new ClientPlayMapMetadataLoaderFactory (
          GameMode.CLASSIC).create (PlayMapType.STOCK, PlayMapType.CUSTOM);
  private static final int WIN_PERCENT_INCREMENT = 5;
  private final MenuScreenWidgetFactory widgetFactory;
  private final Dialog errorDialog;
  private final TextField playerNameTextField;
  private final TextField clanAcronymTextField;
  private final TextField serverNameTextField;
  private final CheckBox clanAcronymCheckBox;
  private final SelectBox <Integer> winPercentSelectBox;
  private final SelectBox <String> initialCountryAssignmentSelectBox;
  private final SelectBox <Integer> spectatorLimitSelectBox;
  private final ImageButton customizeHumanPlayersButton;
  private final ImageButton customizeAiPlayersButton;
  private final ImageButton customizePlayMapButton;
  private final CountryCounter countryCounter;
  private final Label playerSettingsSectionTitleLabel;
  private final Label playerNameSettingLabel;
  private final Label clanTagSettingLabel;
  private final Label gameSettingsSectionTitleLabel;
  private final Label serverNameSettingLabel;
  private final Label humanPlayerLimitSettingLabel;
  private final Label humanPlayerLimitLabel;
  private final Label aiPlayerLimitSettingLabel;
  private final Label aiPlayerLimitLabel;
  private final Label playMapNameLabel;
  private final Label spectatorLimitSettingLabel;
  private final Label playMapSettingLabel;
  private final Label winPercentSettingLabel;
  private final Label initialCountryAssignmentSettingLabel;
  private final Button forwardButton;
  private int totalCountryCount;
  private ImmutableCollection <PlayMapMetadata> playMaps = ImmutableSet.of ();
  private Iterator <PlayMapMetadata> playMapIterator = Collections.emptyIterator ();
  private PlayMapMetadata currentPlayMap = PlayMapMetadata.NULL;
  private boolean isFirstTimeOnScreen = true;

  public ClassicGameModeCreateGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                              final ScreenChanger screenChanger,
                                              final ScreenSize screenSize,
                                              final MouseInput mouseInput,
                                              final Batch batch,
                                              final CountryCounter countryCounter,
                                              final MBassador <Event> eventBus)

  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (countryCounter, "countryCounter");

    this.widgetFactory = widgetFactory;
    this.countryCounter = countryCounter;

    errorDialog = createErrorDialog ();

    addTitle (TITLE_TEXT, Align.bottomLeft, 40);
    addSubTitle (SUBTITLE_TEXT);

    playerNameTextField = widgetFactory.createPlayerNameTextField ();
    clanAcronymTextField = widgetFactory.createClanAcronymTextField ();
    serverNameTextField = widgetFactory.createServerNameTextField ();

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

    // @formatter:off
    humanPlayerLimitLabel = widgetFactory.createPlayerLimitLabel (String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_HUMAN_PLAYER_LIMIT));
    aiPlayerLimitLabel = widgetFactory.createPlayerLimitLabel (String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_AI_PLAYER_LIMIT));
    playMapNameLabel = widgetFactory.createPlayMapNameLabel ();
    totalCountryCount = calculateCurrentPlayMapTotalCountryCount ();
    // @formatter:on

    customizeHumanPlayersButton = widgetFactory.createCustomizePlayersButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Show Players Dialog.

        updateHumanPlayerLimit ();
        updateWinPercentSelectBoxItems ();
      }
    });

    customizeAiPlayersButton = widgetFactory.createCustomizePlayersButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Show Players Dialog.

        updateAiPlayerLimit ();
        updateWinPercentSelectBoxItems ();
      }
    });

    customizePlayMapButton = widgetFactory.createCustomizePlayMapButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Show Play Map Dialog.

        // TODO Add refresh button to Play Map Dialog to re-load play maps.
        playMaps = loadPlayMaps ();

        currentPlayMap = nextPlayMap ();
        playMapNameLabel.setText (currentPlayMap.getName ());
        totalCountryCount = calculateCurrentPlayMapTotalCountryCount ();
        updateWinPercentSelectBoxItems ();
      }
    });

    // @formatter:off
    spectatorLimitSelectBox = widgetFactory.createSpectatorsSelectBox ();
    final Array <Integer> spectatorLimits = new Array <> (ClassicGameRules.MAX_SPECTATORS - ClassicGameRules.MIN_SPECTATORS + 1);
    for (int i = ClassicGameRules.MIN_SPECTATORS; i <= ClassicGameRules.MAX_SPECTATORS; ++i)
    {
      spectatorLimits.add (i);
    }
    spectatorLimitSelectBox.setItems (spectatorLimits);
    spectatorLimitSelectBox.setSelected (InputSettings.INITIAL_SPECTATOR_LIMIT);
    // @formatter:off

    // @formatter:on
    initialCountryAssignmentSelectBox = widgetFactory.createInitialCountryAssignmentSelectBox ();
    final Array <String> initialCountryAssignments = new Array <> (InitialCountryAssignment.count ());
    for (final InitialCountryAssignment initialCountryAssignment : InitialCountryAssignment.values ())
    {
      initialCountryAssignments.add (Strings.toProperCase (initialCountryAssignment.name ()));
    }
    initialCountryAssignmentSelectBox.setItems (initialCountryAssignments);
    initialCountryAssignmentSelectBox.setSelected (Strings
            .toProperCase (InputSettings.INITIAL_CLASSIC_MODE_COUNTRY_ASSIGNMENT.name ()));
    // @formatter:on

    winPercentSelectBox = widgetFactory.createWinPercentSelectBox ();
    updateWinPercentSelectBoxItems ();
    selectInitialWinPercentItem ();

    // @formatter:off
    playerSettingsSectionTitleLabel = widgetFactory.createPlayerSettingsSectionTitleLabel ();
    playerNameSettingLabel = widgetFactory.createPlayerNameSettingLabel ();
    clanTagSettingLabel = widgetFactory.createClanTagSettingLabel ();
    gameSettingsSectionTitleLabel = widgetFactory.createGameSettingsSectionTitleLabel ();
    serverNameSettingLabel = widgetFactory.createMenuSettingLabel (SERVER_NAME_SETTING_LABEL_TEXT);
    humanPlayerLimitSettingLabel = widgetFactory.createMenuSettingLabel (HUMAN_PLAYERS_SETTING_LABEL_TEXT);
    aiPlayerLimitSettingLabel = widgetFactory.createMenuSettingLabel (AI_PLAYERS_SETTING_LABEL_TEXT);
    spectatorLimitSettingLabel = widgetFactory.createMenuSettingLabel (SPECTATORS_SETTING_SETTING_LABEL_TEXT);
    playMapSettingLabel = widgetFactory.createMenuSettingLabel (PLAY_MAP_SETTING_LABEL_TEXT);
    winPercentSettingLabel = widgetFactory.createMenuSettingLabel (WIN_PERCENT_SETTING_LABEL_TEXT);
    initialCountryAssignmentSettingLabel = widgetFactory.createMenuSettingLabel (INITIAL_COUNTRY_ASSIGNMENT_SETTING_LABEL_TEXT);
    // @formatter:on

    final VerticalGroup verticalGroup = new VerticalGroup ();
    verticalGroup.align (Align.topLeft);

    final Table playerSettingsTable = new Table ().top ().left ();
    playerSettingsTable.add ().height (23);
    playerSettingsTable.row ();
    playerSettingsTable.add (playerSettingsSectionTitleLabel).size (540, 40).fill ().padLeft (60).left ();

    playerSettingsTable.row ();

    final Table playerNameTable = new Table ();
    playerNameTable.add (playerNameSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    playerNameTable.add (playerNameTextField).size (270, 28).fill ().left ().spaceLeft (10);
    playerSettingsTable.add (playerNameTable).left ();

    playerSettingsTable.row ();

    final Table clanTable = new Table ();
    clanTable.add (clanTagSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    clanTable.add (clanAcronymCheckBox).size (20, 20).fill ().left ().spaceLeft (10).spaceRight (8);
    clanTable.add (clanAcronymTextField).size (80, 28).fill ().left ().spaceLeft (8);
    playerSettingsTable.add (clanTable).left ();

    verticalGroup.addActor (playerSettingsTable);

    final Table gameSettingsTable = new Table ().top ().left ();
    gameSettingsTable.row ();
    gameSettingsTable.add ().height (20);
    gameSettingsTable.row ();
    gameSettingsTable.add (gameSettingsSectionTitleLabel).size (540, 40).fill ().padLeft (60).left ();

    gameSettingsTable.row ();

    final Table serverNameTable = new Table ();
    serverNameTable.add (serverNameSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    serverNameTable.add (serverNameTextField).size (270, 28).fill ().left ().spaceLeft (10);
    gameSettingsTable.add (serverNameTable).left ();

    gameSettingsTable.row ();

    final Table playMapTable = new Table ();
    playMapTable.add (playMapSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    playMapTable.add (playMapNameLabel).size (238, 28).fill ().left ().spaceLeft (10).spaceRight (4);
    playMapTable.add (customizePlayMapButton).size (28, 28).fill ().left ().spaceLeft (4);
    gameSettingsTable.add (playMapTable).left ();

    gameSettingsTable.row ();

    final Table humanPlayersTable = new Table ();
    humanPlayersTable.add (humanPlayerLimitSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    humanPlayersTable.add (humanPlayerLimitLabel).size (76, 28).fill ().left ().spaceLeft (10).spaceRight (4);
    humanPlayersTable.add (customizeHumanPlayersButton).size (28, 28).fill ().left ().spaceLeft (4);
    gameSettingsTable.add (humanPlayersTable).left ();

    gameSettingsTable.row ();

    final Table aiPlayersTable = new Table ();
    aiPlayersTable.add (aiPlayerLimitSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    aiPlayersTable.add (aiPlayerLimitLabel).size (76, 28).fill ().left ().spaceLeft (10).spaceRight (4);
    aiPlayersTable.add (customizeAiPlayersButton).size (28, 28).fill ().left ().spaceLeft (4);
    gameSettingsTable.add (aiPlayersTable).left ();

    gameSettingsTable.row ();

    final Table spectatorsTable = new Table ();
    spectatorsTable.add (spectatorLimitSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    spectatorsTable.add (spectatorLimitSelectBox).size (108, 28).fill ().left ().spaceLeft (10);
    gameSettingsTable.add (spectatorsTable).left ();

    gameSettingsTable.row ();

    final Table winPercentTable = new Table ();
    winPercentTable.add (winPercentSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    winPercentTable.add (winPercentSelectBox).size (108, 28).fill ().left ().spaceLeft (10);
    gameSettingsTable.add (winPercentTable).left ();

    gameSettingsTable.row ();

    // @formatter:off
    final Table initialCountryAssignmentTable = new Table ();
    initialCountryAssignmentTable.add (initialCountryAssignmentSettingLabel).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    initialCountryAssignmentTable.add (initialCountryAssignmentSelectBox).size (108, 28).fill ().left ().spaceLeft (10);
    gameSettingsTable.add (initialCountryAssignmentTable).left ();
    // @formatter:on

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
            toScreen (ScreenId.CLASSIC_GAME_MODE_MENU);
          }
        });
      }
    });

    forwardButton = addForwardButton (FORWARD_BUTTON_TEXT, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        if (currentPlayMap.equals (PlayMapMetadata.NULL))
        {
          errorDialog.setMessage (new DefaultMessage ("Please select a valid map before continuing."));
          errorDialog.show ();
          return;
        }

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
        final int humanPlayerLimit = getHumanPlayerLimit ();
        final int aiPlayerLimit = getAiPlayerLimit ();
        final int spectatorLimit = getSpectatorLimit ();
        final int winPercent = winPercentSelectBox.getSelected ();
        final InitialCountryAssignment initialCountryAssignment = InitialCountryAssignment.valueOf (Strings
                .toCase (initialCountryAssignmentSelectBox.getSelected (), LetterCase.UPPER));
        final PersonLimits personLimits = PersonLimits.builder ().humanPlayers (humanPlayerLimit)
                .aiPlayers (aiPlayerLimit).spectators (spectatorLimit).build ();
        final GameRules gameRules = GameRulesFactory.create (GameMode.CLASSIC, personLimits, winPercent,
                                                             totalCountryCount, initialCountryAssignment);
        final GameConfiguration gameConfig = new DefaultGameConfiguration (GameMode.CLASSIC, currentPlayMap, gameRules);
        final String serverName = serverNameTextField.getText ();

        if (!NetworkSettings.isValidServerName (serverName))
        {
          errorDialog.setMessage (new DefaultMessage (Strings
                  .format ("Invalid server name: \'{}\'\n\nValid server name rules:\n\n{}", serverName,
                           NetworkSettings.VALID_SERVER_NAME_DESCRIPTION)));
          errorDialog.show ();
          return;
        }

        toScreen (ScreenId.MENU_TO_PLAY_LOADING);

        // The menu-to-play loading screen is now active & can therefore receive events.

        publishAsync (new CreateGameEvent (serverName, gameConfig, playerNameWithOptionalClanTag));
      }
    });
  }

  @Override
  public void show ()
  {
    super.show ();

    expandMenuBar ();

    playMapIterator = Collections.emptyIterator ();
    playMaps = loadPlayMaps ();
    currentPlayMap = findPlayMapOrFirstPlayMap (InputSettings.INITIAL_CLASSIC_MODE_PLAY_MAP_NAME);
    playMapNameLabel.setText (currentPlayMap.getName ());
    totalCountryCount = calculateCurrentPlayMapTotalCountryCount ();
    updateWinPercentSelectBoxItems ();

    // @formatter:off
    playerNameTextField.setStyle (widgetFactory.createPlayerNameTextFieldStyle ());
    clanAcronymTextField.setStyle (widgetFactory.createClanAcronymTextFieldStyle ());
    serverNameTextField.setStyle (widgetFactory.createServerNameTextFieldStyle ());
    clanAcronymCheckBox.setStyle (widgetFactory.createClanAcronymCheckBoxStyle ());
    final SelectBox.SelectBoxStyle winPercentSelectBoxStyle = widgetFactory.createWinPercentSelectBoxStyle ();
    winPercentSelectBox.setStyle (winPercentSelectBoxStyle);
    final SelectBox.SelectBoxStyle spectatorLimitSelectBoxStyle = widgetFactory.createSpectatorLimitSelectBoxStyle ();
    spectatorLimitSelectBox.setStyle (spectatorLimitSelectBoxStyle);
    final SelectBox.SelectBoxStyle initialCountryAssignmentSelectBoxStyle = widgetFactory.createInitialCountryAssignmentSelectBoxStyle ();
    initialCountryAssignmentSelectBox.setStyle (initialCountryAssignmentSelectBoxStyle);
    playMapNameLabel.setStyle (widgetFactory.createPlayMapNameLabelStyle ());
    customizeHumanPlayersButton.setStyle (widgetFactory.createCustomizePlayersButtonStyle ());
    customizeAiPlayersButton.setStyle (widgetFactory.createCustomizePlayersButtonStyle ());
    customizePlayMapButton.setStyle (widgetFactory.createCustomizePlayMapButtonStyle ());
    playerSettingsSectionTitleLabel.setStyle (widgetFactory.createPlayerSettingsSectionTitleLabelStyle ());
    playerNameSettingLabel.setStyle (widgetFactory.createPlayerNameSettingLabelStyle ());
    clanTagSettingLabel.setStyle (widgetFactory.createClanTagSettingLabelStyle ());
    gameSettingsSectionTitleLabel.setStyle (widgetFactory.createGameSettingsSectionTitleLabelStyle ());
    serverNameSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    humanPlayerLimitSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    humanPlayerLimitLabel.setStyle (widgetFactory.createPlayerLimitLabelStyle ());
    aiPlayerLimitSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    aiPlayerLimitLabel.setStyle (widgetFactory.createPlayerLimitLabelStyle ());
    spectatorLimitSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    playMapSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    winPercentSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    initialCountryAssignmentSettingLabel.setStyle (widgetFactory.createMenuSettingLabelStyle ());
    // @formatter:on

    if (isFirstTimeOnScreen && InputSettings.AUTO_CREATE_GAME)
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
          toScreen (ScreenId.CLASSIC_GAME_MODE_MENU);
        }
      });
    }
    return true;
  }

  private ImmutableSet <PlayMapMetadata> loadPlayMaps ()
  {
    try
    {
      return PLAY_MAPS_LOADER.load ();
    }
    catch (final PlayMapLoadingException e)
    {
      final String errorMessage = Strings
              .format ("There was a problem loading map data.\n\nProblem:\n\n{}\n\nDetails\n\n{}", Throwables
                      .getRootCause (e).getMessage (), Strings.toString (e));

      log.error (errorMessage);

      errorDialog.setMessage (new DefaultMessage (errorMessage));
      errorDialog.show ();

      return ImmutableSet.of (PlayMapMetadata.NULL);
    }
  }

  private int calculateCurrentPlayMapTotalCountryCount ()
  {
    try
    {
      return countryCounter.count (currentPlayMap);
    }
    catch (final PlayMapLoadingException e)
    {
      final String errorMessage = Strings
              .format ("Could not read country data for {} map \'{}\'.\n\nProblem:\n\n{}\n\nDetails\n\n{}",
                       currentPlayMap.getType ().name ().toLowerCase (), currentPlayMap.getName (), Throwables
                               .getRootCause (e).getMessage (), Strings.toString (e));

      log.error (errorMessage);

      errorDialog.setMessage (new DefaultMessage (errorMessage));
      errorDialog.show ();

      return ClassicGameRules.DEFAULT_TOTAL_COUNTRY_COUNT;
    }
  }

  private void updateWinPercentSelectBoxItems ()
  {
    if (currentPlayMap.equals (PlayMapMetadata.NULL))
    {
      winPercentSelectBox.setItems (ClassicGameRules.MAX_WIN_PERCENTAGE);
      return;
    }

    // @formatter:off
    final GameRules gameRules = ClassicGameRules.builder ()
            .totalCountryCount (totalCountryCount)
            .humanPlayerLimit (getHumanPlayerLimit ())
            .aiPlayerLimit (getAiPlayerLimit ())
            .spectatorLimit (getSpectatorLimit ())
            .winPercentage (ClassicGameRules.MAX_WIN_PERCENTAGE)
            .initialCountryAssignment (InitialCountryAssignment.valueOf (initialCountryAssignmentSelectBox.getSelected ().toUpperCase ()))
            .build ();

    final Array <Integer> winPercentCounts =
            new Array <> (gameRules.getMaxWinPercentage () - gameRules.getMinWinPercentage () + 1);

    for (int i = Maths.nextHigherMultiple (gameRules.getMinWinPercentage (), WIN_PERCENT_INCREMENT);
         i <= gameRules.getMaxWinPercentage (); i += WIN_PERCENT_INCREMENT)
    {
      winPercentCounts.add (i);
    }
    // @formatter:on

    winPercentSelectBox.setItems (winPercentCounts);
  }

  private void selectInitialWinPercentItem ()
  {
    winPercentSelectBox.setSelected (InputSettings.INITIAL_CLASSIC_MODE_WIN_PERCENT);

    if (winPercentSelectBox.getItems ().contains (InputSettings.INITIAL_CLASSIC_MODE_WIN_PERCENT, true)) return;

    errorDialog.setMessage (new DefaultMessage (Strings
            .format ("{} % is not a valid win percent for {} players on {} map: \'{}\'.\n\n"
                             + "Please check your settings file.", InputSettings.INITIAL_CLASSIC_MODE_WIN_PERCENT,
                     getValidTotalPlayerLimit (), currentPlayMap.getType ().name ().toLowerCase (),
                     currentPlayMap.getName ())));

    errorDialog.show ();
  }

  private PlayMapMetadata nextPlayMap ()
  {
    if (!playMapIterator.hasNext ()) playMapIterator = playMaps.iterator ();

    if (!playMapIterator.hasNext ())
    {
      errorDialog.setMessage (new DefaultMessage (Strings.format ("No maps could be found.")));
      errorDialog.show ();
      return PlayMapMetadata.NULL;
    }

    return playMapIterator.next ();
  }

  private PlayMapMetadata findPlayMapOrFirstPlayMap (final String playMapName)
  {
    final PlayMapMetadata firstPlayMap = nextPlayMap ();
    PlayMapMetadata playMap = firstPlayMap;

    while (!playMap.getName ().equalsIgnoreCase (InputSettings.INITIAL_CLASSIC_MODE_PLAY_MAP_NAME))
    {
      playMap = nextPlayMap ();

      if (playMap.equals (firstPlayMap))
      {
        errorDialog.setMessage (new DefaultMessage (Strings
                .format ("Could not find any map named \'{}\'.\n\nPlease check your settings file. ", playMapName)));
        errorDialog.show ();
        playMapIterator = Collections.emptyIterator ();
        return nextPlayMap ();
      }
    }

    return playMap;
  }

  private void updateHumanPlayerLimit ()
  {
    setHumanPlayerLimit (canIncreaseHumanPlayerLimit () ? getHumanPlayerLimit () + 1 : 0);
  }

  private void updateAiPlayerLimit ()
  {
    setAiPlayerLimit (canIncreaseAiPlayerLimit () ? getAiPlayerLimit () + 1 : 0);
  }

  private boolean canIncreaseHumanPlayerLimit ()
  {
    return getHumanPlayerLimit () < ClassicGameRules.MAX_HUMAN_PLAYER_LIMIT
            && getValidTotalPlayerLimit () < ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT;
  }

  private boolean canIncreaseAiPlayerLimit ()
  {
    return getAiPlayerLimit () < ClassicGameRules.MAX_AI_PLAYER_LIMIT
            && getValidTotalPlayerLimit () < ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT;
  }

  private int getHumanPlayerLimit ()
  {
    return Integer.valueOf (humanPlayerLimitLabel.getText ().toString ());
  }

  private void setHumanPlayerLimit (final int limit)
  {
    assert limit >= 0;
    humanPlayerLimitLabel.setText (String.valueOf (limit));
  }

  private int getAiPlayerLimit ()
  {
    return Integer.valueOf (aiPlayerLimitLabel.getText ().toString ());
  }

  private void setAiPlayerLimit (final int limit)
  {
    assert limit >= 0;
    aiPlayerLimitLabel.setText (String.valueOf (limit));
  }

  private int getValidTotalPlayerLimit ()
  {
    return getValidTotalPlayerLimit (getHumanPlayerLimit (), getAiPlayerLimit ());
  }

  private int getValidTotalPlayerLimit (final int humanPlayerLimit, final int aiPlayerLimit)
  {
    assert humanPlayerLimit >= 0;
    assert aiPlayerLimit >= 0;

    final int limit = humanPlayerLimit + aiPlayerLimit;

    if (limit < ClassicGameRules.MIN_TOTAL_PLAYER_LIMIT) return ClassicGameRules.MIN_TOTAL_PLAYER_LIMIT;
    if (limit > ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT) return ClassicGameRules.MAX_TOTAL_PLAYER_LIMIT;

    return limit;
  }

  private int getSpectatorLimit ()
  {
    final Integer spectatorLimit = spectatorLimitSelectBox.getSelected ();

    return spectatorLimit != null ? spectatorLimit : ClassicGameRules.MIN_SPECTATOR_LIMIT;
  }
}
