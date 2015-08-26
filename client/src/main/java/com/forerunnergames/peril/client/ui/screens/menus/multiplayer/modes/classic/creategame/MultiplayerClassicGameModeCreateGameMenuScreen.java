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

import com.forerunnergames.peril.client.events.CreateGameEvent;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.CountryCounter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.ClientMapMetadataLoaderFactory;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListenerAdapter;
import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.map.io.MapMetadataLoader;
import com.forerunnergames.peril.common.net.settings.NetworkSettings;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Maths;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public final class MultiplayerClassicGameModeCreateGameMenuScreen extends AbstractMenuScreen
{
  // @formatter:off
  private static final MapMetadataLoader MAPS_LOADER =
          new ClientMapMetadataLoaderFactory (GameMode.CLASSIC).create (MapType.STOCK, MapType.CUSTOM);
  // @formatter:on
  private static final int WIN_PERCENT_INCREMENT = 5;
  private final Popup errorPopup;
  private final TextField playerNameTextField;
  private final TextField playerClanTagTextField;
  private final TextField serverNameTextField;
  private final CheckBox playerClanTagCheckBox;
  private final SelectBox <Integer> winPercentSelectBox;
  private final SelectBox <String> initialCountryAssignmentSelectBox;
  private final Label playerLimitLabel;
  private final Label mapNameLabel;
  private final CountryCounter countryCounter;
  private Set <MapMetadata> maps;
  private int totalCountryCount;
  @Nullable
  private Iterator <MapMetadata> mapIterator = null;
  private MapMetadata currentMap = MapMetadata.NULL_MAP_METADATA;

  public MultiplayerClassicGameModeCreateGameMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                                         final ScreenChanger screenChanger,
                                                         final ScreenSize screenSize,
                                                         final Batch batch,
                                                         final CountryCounter countryCounter,
                                                         final MBassador <Event> eventBus)

  {
    super (widgetFactory, screenChanger, screenSize, batch);

    Arguments.checkIsNotNull (countryCounter, "countryCounter");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.countryCounter = countryCounter;

    errorPopup = createErrorPopup (new PopupListenerAdapter ());

    addTitle ("CREATE MULTIPLAYER GAME", Align.bottomLeft, 40);
    addSubTitle ("CLASSIC MODE", Align.topLeft, 40);

    // @formatter:off

    playerNameTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_NAME_LENGTH, GameSettings.PLAYER_NAME_PATTERN);
    playerClanTagTextField = widgetFactory.createTextField (GameSettings.MAX_PLAYER_CLAN_TAG_LENGTH, GameSettings.PLAYER_CLAN_TAG_PATTERN);
    serverNameTextField = widgetFactory.createTextField (NetworkSettings.MAX_SERVER_NAME_LENGTH, NetworkSettings.SERVER_NAME_PATTERN);

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

    playerLimitLabel = widgetFactory.createBackgroundLabel (String.valueOf (ClassicGameRules.MIN_PLAYER_LIMIT), Align.left);

    maps = loadMaps ();
    currentMap = nextMap ();
    mapNameLabel = widgetFactory.createBackgroundLabel (asMapNameLabelText (currentMap), Align.left);
    totalCountryCount = calculateCurrentMapTotalCountryCount ();

    final ImageButton customizePlayersButton = widgetFactory.createImageButton ("options",
            new ClickListener (Input.Buttons.LEFT)
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

                updateWinPercentSelectBoxItems ();
              }
            });

    final ImageButton customizeMapButton = widgetFactory.createImageButton ("options",
            new ClickListener (Input.Buttons.LEFT)
            {
              @Override
              public void clicked (final InputEvent event, final float x, final float y)
              {
                // TODO Implement CustomizeMapPopup.

                currentMap = nextMap ();
                mapNameLabel.setText (asMapNameLabelText (currentMap));
                totalCountryCount = calculateCurrentMapTotalCountryCount ();
                updateWinPercentSelectBoxItems ();
              }
            });

    final SelectBox <Integer> spectatorsSelectBox = widgetFactory.createSelectBox ();
    final Array <Integer> spectatorCounts = new Array <> (GameSettings.MAX_SPECTATORS - GameSettings.MIN_SPECTATORS + 1);
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
    updateWinPercentSelectBoxItems ();
    winPercentSelectBox.setSelected (ClassicGameRules.MAX_WIN_PERCENTAGE);

    final VerticalGroup verticalGroup = new VerticalGroup ();
    verticalGroup.align (Align.topLeft);

    final Table playerSettingsTable = new Table ().top ().left ();
    playerSettingsTable.add ().height (23).colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createMenuSettingSectionTitleText ("Your Player")).size (538, 42).fill ().padLeft (60).padRight (60).left ().colspan (5);
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createMenuSettingText ("Name")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    playerSettingsTable.add (playerNameTextField).size (204, 28).fill ().left ().colspan (3).spaceLeft (10);
    playerSettingsTable.add ().expandX ().fill ();
    playerSettingsTable.row ();
    playerSettingsTable.add (widgetFactory.createMenuSettingText ("Clan Tag")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    playerSettingsTable.add (playerClanTagCheckBox).size (18, 18).fill ().left ().spaceLeft (10).spaceRight (10);
    playerSettingsTable.add (playerClanTagTextField).size (74, 28).fill ().left ().spaceLeft (10);
    playerSettingsTable.add ().width (102).fill ();
    playerSettingsTable.add ().expandX ().fill ();
    verticalGroup.addActor (playerSettingsTable);

    final Table gameSettingsTable = new Table ().top ().left ();
    gameSettingsTable.row ();
    gameSettingsTable.add ().height (18).colspan (5);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingSectionTitleText ("Game Settings")).size (538, 42).fill ().padLeft (60).padRight (60).left ().colspan (5);
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Title")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (serverNameTextField).size (204, 28).fill ().left ().colspan (3).spaceLeft (10);
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Players")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (playerLimitLabel).size (70, 28).fill ().left ().spaceLeft (10).spaceRight (4);
    gameSettingsTable.add (customizePlayersButton).size (28, 28).fill ().left ().spaceLeft (4);
    gameSettingsTable.add ().width (102).fill ();
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Spectators")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (spectatorsSelectBox).size (102, 28).fill ().left ().spaceLeft (10).colspan (2);
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Map")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (mapNameLabel).size (70, 28).fill ().left ().spaceLeft (10).spaceRight (4);
    gameSettingsTable.add (customizeMapButton).size (28, 28).fill ().left ().spaceLeft (4);
    gameSettingsTable.add ().width (102).fill ();
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Win Percent")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
    gameSettingsTable.add (winPercentSelectBox).size (102, 28).fill ().left ().spaceLeft (10).colspan (2);
    gameSettingsTable.add ().expandX ().fill ();
    gameSettingsTable.row ();
    gameSettingsTable.add (widgetFactory.createMenuSettingText ("Initial Countries")).size (150, 40).fill ().padLeft (90).left ().spaceRight (10);
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
        if (currentMap.equals (MapMetadata.NULL_MAP_METADATA))
        {
          errorPopup.setMessage (new DefaultMessage ("Please select a valid map before continuing."));
          errorPopup.show ();
          return;
        }

        final String rawPlayerName = playerNameTextField.getText ();
        final String rawPlayerClanTag = playerClanTagTextField.getText ();
        final String finalPlayerName = rawPlayerClanTag.isEmpty () ? rawPlayerName :
                        GameSettings.PLAYER_CLAN_TAG_START_SYMBOL + rawPlayerClanTag + GameSettings.PLAYER_CLAN_TAG_END_SYMBOL + " " + rawPlayerName;
        final String finalServerName = serverNameTextField.getText ();
        final int finalPlayerLimit = Integer.valueOf (playerLimitLabel.getText ().toString ());
        final int finalWinPercent = winPercentSelectBox.getSelected ();
        final InitialCountryAssignment finalInitialCountryAssignment =
                InitialCountryAssignment.valueOf (Strings.toCase (initialCountryAssignmentSelectBox.getSelected (), LetterCase.UPPER));
        final GameConfiguration gameConfig =
                new DefaultGameConfiguration (GameMode.CLASSIC, finalPlayerLimit, finalWinPercent, finalInitialCountryAssignment, currentMap);

        toScreen (ScreenId.LOADING);

        eventBus.publish (new CreateGameEvent (finalServerName, gameConfig, finalPlayerName));
      }
    });
    // @formatter:on
  }

  @Override
  public void show ()
  {
    super.show ();

    expandMenuBar ();

    mapIterator = null;
    maps = loadMaps ();
    currentMap = nextMap ();
    mapNameLabel.setText (asMapNameLabelText (currentMap));
    totalCountryCount = calculateCurrentMapTotalCountryCount ();
    updateWinPercentSelectBoxItems ();
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

  private static String asMapNameLabelText (final MapMetadata map)
  {
    return Strings.toProperCase (map.getName ());
  }

  private ImmutableSet <MapMetadata> loadMaps ()
  {
    try
    {
      return MAPS_LOADER.load ();
    }
    catch (final PlayMapLoadingException e)
    {
      errorPopup.setMessage (new DefaultMessage (
              Strings.format ("There was a problem loading map data.\n\nProblem:\n\n{}\n\nDetails\n\n{}",
                              Throwables.getRootCause (e).getMessage (), Strings.toString (e))));

      errorPopup.show ();

      return ImmutableSet.of (MapMetadata.NULL_MAP_METADATA);
    }
  }

  private int calculateCurrentMapTotalCountryCount ()
  {
    try
    {
      return countryCounter.count (currentMap);
    }
    catch (final PlayMapLoadingException e)
    {
      errorPopup.setMessage (new DefaultMessage (Strings
              .format ("Could not read country data for {} map \'{}\'.\n\nProblem:\n\n{}\n\nDetails\n\n{}",
                       currentMap.getType ().name ().toLowerCase (), Strings.toProperCase (currentMap.getName ()),
                       Throwables.getRootCause (e).getMessage (), Strings.toString (e))));

      errorPopup.show ();

      return ClassicGameRules.DEFAULT_TOTAL_COUNTRY_COUNT;
    }
  }

  private void updateWinPercentSelectBoxItems ()
  {
    if (currentMap.equals (MapMetadata.NULL_MAP_METADATA))
    {
      winPercentSelectBox.setItems (ClassicGameRules.MAX_WIN_PERCENTAGE);
      return;
    }

    // @formatter:off
    final GameRules gameRules = new ClassicGameRules.Builder ()
            .totalCountryCount (totalCountryCount)
            .playerLimit (Integer.valueOf (playerLimitLabel.getText ().toString ()))
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

  private MapMetadata nextMap ()
  {
    if (mapIterator == null || !mapIterator.hasNext ()) mapIterator = maps.iterator ();

    if (!mapIterator.hasNext ())
    {
      errorPopup.setMessage (new DefaultMessage (Strings.format ("No maps could be found.")));
      errorPopup.show ();
      return MapMetadata.NULL_MAP_METADATA;
    }

    return mapIterator.next ();
  }
}
