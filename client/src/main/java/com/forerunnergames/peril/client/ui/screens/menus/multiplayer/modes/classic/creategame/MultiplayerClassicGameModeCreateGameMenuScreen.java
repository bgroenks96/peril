package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

import com.forerunnergames.peril.client.io.CountryNamesDataLoader;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.peril.core.shared.settings.AssetSettings;
import com.forerunnergames.peril.core.shared.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Maths;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiplayerClassicGameModeCreateGameMenuScreen extends AbstractMenuScreen
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerClassicGameModeCreateGameMenuScreen.class);
  private static final int WIN_PERCENT_INCREMENT = 5;
  private final CountryNamesDataLoader countryNamesDataLoader;
  private final TextField playerNameTextField;
  private final TextField playerClanTagTextField;
  private final TextField serverNameTextField;
  private final CheckBox playerClanTagCheckBox;
  private final SelectBox <Integer> spectatorsSelectBox;
  private final SelectBox <Integer> winPercentSelectBox;
  private final SelectBox <String> initialCountryAssignmentSelectBox;
  private final Label playerLimitLabel;
  private final Label mapNameLabel;
  private final ImageButton customizePlayersButton;
  private final ImageButton customizeMapButton;
  private final ImmutableSet <String> mapNames;
  private Iterator <String> mapNameIterator;
  private int totalCountryCount = ClassicGameRules.DEFAULT_TOTAL_COUNTRY_COUNT;
  private String currentMapName;

  public MultiplayerClassicGameModeCreateGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                                         final ScreenChanger screenChanger,
                                                         final ScreenSize screenSize,
                                                         final Batch batch,
                                                         final CreateGameHandler createGameHandler,
                                                         final CountryNamesDataLoader countryNamesDataLoader)
  {
    super (widgetFactory, screenChanger, screenSize, batch);

    Arguments.checkIsNotNull (createGameHandler, "createGameHandler");
    Arguments.checkIsNotNull (countryNamesDataLoader, "countryNamesDataLoader");

    this.countryNamesDataLoader = countryNamesDataLoader;

    addTitle ("CREATE MULTIPLAYER GAME", Align.bottomLeft, 40);
    addSubTitle ("CLASSIC MODE", Align.topLeft, 40);

    playerNameTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_NAME_LENGTH,
                                                         GameSettings.PLAYER_NAME_PATTERN);

    playerClanTagTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_CLAN_TAG_LENGTH,
                                                            GameSettings.PLAYER_CLAN_TAG_PATTERN);

    serverNameTextField = widgetFactory.createTextField (NetworkSettings.MAX_SERVER_NAME_LENGTH,
                                                         NetworkSettings.SERVER_NAME_PATTERN);

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

    playerLimitLabel = widgetFactory.createBackgroundLabel (String.valueOf (ClassicGameRules.MIN_PLAYER_LIMIT),
                                                            Align.left);

    mapNames = loadMapNames ();
    currentMapName = nextMapName ();
    mapNameLabel = widgetFactory.createBackgroundLabel (currentMapName, Align.left);
    updateTotalCountryCount ();

    customizePlayersButton = widgetFactory.createImageButton ("options", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Implement CustomizePlayersPopup.

        if (Integer.valueOf (playerLimitLabel.getText ().toString ()) < ClassicGameRules.MAX_PLAYERS)
        {
          playerLimitLabel.setText (String.valueOf (Integer.valueOf (playerLimitLabel.getText ().toString ()) + 1));
        }
        else
        {
          playerLimitLabel.setText (String.valueOf (ClassicGameRules.MIN_PLAYER_LIMIT));
        }

        updateWinPercentSelectBox ();
      }
    });

    customizeMapButton = widgetFactory.createImageButton ("options", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Implement CustomizeMapPopup.

        currentMapName = nextMapName ();
        mapNameLabel.setText (currentMapName);

        updateTotalCountryCount ();
        updateWinPercentSelectBox ();
      }
    });

    spectatorsSelectBox = widgetFactory.createSelectBox ();
    final Array <Integer> spectatorCounts = new Array <> (
            GameSettings.MAX_SPECTATORS - GameSettings.MIN_SPECTATORS + 1);
    for (int i = GameSettings.MIN_SPECTATORS; i <= GameSettings.MAX_SPECTATORS; ++i)
    {
      spectatorCounts.add (i);
    }
    spectatorsSelectBox.setItems (spectatorCounts);
    spectatorsSelectBox.setSelected (GameSettings.MIN_SPECTATORS);

    initialCountryAssignmentSelectBox = widgetFactory.createSelectBox ();
    final Array <String> initialCountryAssignments = new Array <> (InitialCountryAssignment.count ());
    for (final InitialCountryAssignment initialCountryAssignment : InitialCountryAssignment.values ())
    {
      initialCountryAssignments.add (initialCountryAssignment.toProperCase ());
    }
    initialCountryAssignmentSelectBox.setItems (initialCountryAssignments);
    initialCountryAssignmentSelectBox.setSelected (ClassicGameRules.DEFAULT_INITIAL_COUNTRY_ASSIGNMENT.toProperCase ());

    winPercentSelectBox = widgetFactory.createSelectBox ();
    updateWinPercentSelectBox ();
    winPercentSelectBox.setSelected (ClassicGameRules.MAX_WIN_PERCENTAGE);

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
    gameSettingsTable.add ().height (18).colspan (5);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingSectionTitleText ("Game Settings")).size (538, 42).fill ()
            .padLeft (60).padRight (60).left ().colspan (5);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Title")).size (150, 40).fill ().padLeft (90).left ()
            .spaceRight (10);
    gameSettingsTable.add (serverNameTextField).size (204, 28).fill ().left ().colspan (3).spaceLeft (10);
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Players")).size (150, 40).fill ().padLeft (90).left ()
            .spaceRight (10);
    gameSettingsTable.add (playerLimitLabel).size (70, 28).fill ().left ().spaceLeft (10).spaceRight (4);
    gameSettingsTable.add (customizePlayersButton).size (28, 28).fill ().left ().spaceLeft (4);
    gameSettingsTable.add ().width (102).fill ();
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Spectators")).size (150, 40).fill ().padLeft (90)
            .left ().spaceRight (10);
    gameSettingsTable.add (spectatorsSelectBox).size (102, 28).fill ().left ().spaceLeft (10).colspan (2);
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Map")).size (150, 40).fill ().padLeft (90).left ()
            .spaceRight (10);
    gameSettingsTable.add (mapNameLabel).size (70, 28).fill ().left ().spaceLeft (10).spaceRight (4);
    gameSettingsTable.add (customizeMapButton).size (28, 28).fill ().left ().spaceLeft (4);
    gameSettingsTable.add ().width (102).fill ();
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Win Percent")).size (150, 40).fill ().padLeft (90)
            .left ().spaceRight (10);
    gameSettingsTable.add (winPercentSelectBox).size (102, 28).fill ().left ().spaceLeft (10).colspan (2);
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Initial Countries")).size (150, 40).fill ()
            .padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (initialCountryAssignmentSelectBox).size (102, 28).fill ().left ().spaceLeft (10).colspan (2);
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

    addForwardButton ("CREATE GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        final String rawPlayerName = playerNameTextField.getText ();
        final String rawPlayerClanTag = playerClanTagTextField.getText ();
        final String finalPlayerName = rawPlayerClanTag.isEmpty () ? rawPlayerName
                : GameSettings.PLAYER_CLAN_TAG_START_SYMBOL + rawPlayerClanTag + GameSettings.PLAYER_CLAN_TAG_END_SYMBOL
                        + " " + rawPlayerName;
        final String finalServerName = serverNameTextField.getText ();
        final int finalPlayerLimit = Integer.valueOf (playerLimitLabel.getText ().toString ());
        final int finalWinPercent = winPercentSelectBox.getSelected ();
        final InitialCountryAssignment finalInitialCountryAssignment = InitialCountryAssignment
                .valueOf (Strings.toCase (initialCountryAssignmentSelectBox.getSelected (), LetterCase.UPPER));

        // TODO Pass currentMapName into GameConfiguration
        final GameConfiguration gameConfig = new DefaultGameConfiguration (GameMode.CLASSIC, finalPlayerLimit,
                finalWinPercent, finalInitialCountryAssignment);

        // TODO Go to loading screen

        createGameHandler.createGame (finalServerName, gameConfig, finalPlayerName);
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

  private static ImmutableSet <String> loadMapNames ()
  {
    final ImmutableSet.Builder <String> mapNamesBuilder = ImmutableSet.builder ();
    final File classicModeMapsDirectory = new File (AssetSettings.ABSOLUTE_EXTERNAL_CLASSIC_MODE_MAPS_DIRECTORY);
    final File[] childPathFiles = classicModeMapsDirectory.listFiles ();

    if (childPathFiles == null) cannotFindAnyMapsIn (classicModeMapsDirectory);

    for (final File childPathFile : childPathFiles)
    {
      if (childPathFile.isDirectory ()) mapNamesBuilder.add (Strings.toProperCase (childPathFile.getName ()));
    }

    final ImmutableSet <String> mapNames = mapNamesBuilder.build ();

    if (mapNames.isEmpty ()) cannotFindAnyMapsIn (classicModeMapsDirectory);

    return mapNames;
  }

  private static void cannotFindAnyMapsIn (final File directory)
  {
    throw new IllegalStateException (Strings.format ("Cannot find any maps in {}", directory));
  }

  private void updateWinPercentSelectBox ()
  {
    final GameRules gameRules = new ClassicGameRules.Builder ().totalCountryCount (totalCountryCount)
            .playerLimit (Integer.valueOf (playerLimitLabel.getText ().toString ()))
            .winPercentage (ClassicGameRules.MAX_WIN_PERCENTAGE).initialCountryAssignment (InitialCountryAssignment
                    .valueOf (initialCountryAssignmentSelectBox.getSelected ().toUpperCase ()))
            .build ();

    final Array <Integer> winPercentCounts = new Array <> (
            gameRules.getMaxWinPercentage () - gameRules.getMinWinPercentage () + 1);

    for (int i = Maths.nextHigherMultiple (gameRules.getMinWinPercentage (), WIN_PERCENT_INCREMENT); i <= gameRules
            .getMaxWinPercentage (); i += WIN_PERCENT_INCREMENT)
    {
      winPercentCounts.add (i);
    }

    winPercentSelectBox.setItems (winPercentCounts);
  }

  private void updateTotalCountryCount ()
  {
    final String mapName = mapNameLabel.getText ().toString ().toLowerCase ();

    totalCountryCount = countryNamesDataLoader.load ("screens/game/play/modes/classic/maps/" + mapName
            + "/countries/data/" + com.forerunnergames.peril.core.shared.settings.AssetSettings.COUNTRY_DATA_FILENAME)
            .size ();
  }

  private String nextMapName ()
  {
    if (mapNameIterator == null || !mapNameIterator.hasNext ()) mapNameIterator = mapNames.iterator ();

    return mapNameIterator.next ();
  }
}
