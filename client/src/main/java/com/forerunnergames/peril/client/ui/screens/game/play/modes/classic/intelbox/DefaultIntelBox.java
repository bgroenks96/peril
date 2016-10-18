package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.playercoloricons.PlayerColorIcon;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;

import javax.annotation.Nullable;

public final class DefaultIntelBox implements IntelBox
{
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final Table intelBoxTable;
  private final Table titleTable;
  private final Label titleLabel;
  private final Label playerNameSettingLabel;
  private final Label playerNameTextLabel;
  private final Label serverNameSettingLabel;
  private final Label serverNameTextLabel;
  private final Label mapNameSettingLabel;
  private final Label mapNameTextLabel;
  private final Label gameRoundSettingLabel;
  private final Label gameRoundTextLabel;
  private final Label gamePhaseSettingLabel;
  private final Label gamePhaseTextLabel;
  private final Label conquerSettingLabel;
  private final Label conquerWinPercentTextLabel;
  private final Label conquerCountryCountTextLabel;
  private final Label reinforcementsSettingLabel;
  private final Label countryReinforcementsSettingLabel;
  private final Label countryReinforcementsTextLabel;
  private final Label continentReinforcementsSettingLabel;
  private final Label continentReinforcementsTextLabel;
  private final Label subtotalReinforcementsSettingLabel;
  private final Label subtotalReinforcementsTextLabel;
  private final Label tradeInReinforcementsSettingLabel;
  private final Label tradeInReinforcementsTextLabel;
  private final Label totalReinforcementsSettingLabel;
  private final Label totalReinforcementsTextLabel;
  private final Label detailedReportButtonLabel;
  private final ImageButton detailedReportButton;
  private final Cell <Actor> playerColorIconCell;
  private final Table playerNameTable;
  private PlayerColorIcon playerColorIcon = PlayerColorIcon.NULL_PLAYER_COLOR_ICON;
  private int ownedCountries;
  @Nullable
  private GameServerConfiguration gameServerConfig;
  @Nullable
  private PlayerPacket selfPlayer;

  public DefaultIntelBox (final ClassicModePlayScreenWidgetFactory widgetFactory,
                          final EventListener detailedReportButtonListener)
  {
    Arguments.checkIsNotNull (detailedReportButtonListener, "detailedReportButtonListener");

    this.widgetFactory = widgetFactory;

    titleLabel = widgetFactory.createIntelBoxTitleLabel ("Intel");
    playerNameSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Player: ");
    playerNameTextLabel = widgetFactory.createIntelBoxSettingTextWrappingLabel ("?");
    serverNameSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Server: ");
    serverNameTextLabel = widgetFactory.createIntelBoxSettingTextWrappingLabel ("?");
    mapNameSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Map: ");
    mapNameTextLabel = widgetFactory.createIntelBoxSettingTextWrappingLabel ("?");
    gameRoundSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Round: ");
    gameRoundTextLabel = widgetFactory.createIntelBoxSettingTextLabel ("?");
    gamePhaseSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Phase: ");
    gamePhaseTextLabel = widgetFactory.createIntelBoxSettingTextLabel ("?");
    conquerSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Ownership: ");
    conquerWinPercentTextLabel = widgetFactory.createIntelBoxSettingTextLabel ("? of ? %");
    conquerCountryCountTextLabel = widgetFactory.createIntelBoxSettingTextLabel ("* Conquer ? more countries to win");
    reinforcementsSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Estimated Reinforcements:");
    countryReinforcementsSettingLabel = widgetFactory.createIntelBoxSettingTextLabel ("* Countries: ");
    countryReinforcementsTextLabel = widgetFactory.createIntelBoxSettingTextLabel ("?", Align.right);
    continentReinforcementsSettingLabel = widgetFactory.createIntelBoxSettingTextLabel ("* Continents: ");
    continentReinforcementsTextLabel = widgetFactory.createIntelBoxSettingTextLabel ("?", Align.right);
    subtotalReinforcementsSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Subtotal: ");
    subtotalReinforcementsTextLabel = widgetFactory.createIntelBoxSettingNameLabel ("?", Align.right);
    tradeInReinforcementsSettingLabel = widgetFactory.createIntelBoxSettingTextLabel ("* Purchases: ");
    tradeInReinforcementsTextLabel = widgetFactory.createIntelBoxSettingTextLabel ("?", Align.right);
    totalReinforcementsSettingLabel = widgetFactory.createIntelBoxSettingNameLabel ("Total: ");
    totalReinforcementsTextLabel = widgetFactory.createIntelBoxSettingNameLabel ("?", Align.right);
    detailedReportButtonLabel = widgetFactory.createIntelBoxButtonTextLabel ("Detailed Report");
    detailedReportButton = widgetFactory.createIntelBoxDetailedReportButton (detailedReportButtonListener);

    intelBoxTable = new Table ().top ().left ().pad (4);
    intelBoxTable.setBackground (widgetFactory.createIntelBoxBackgroundDrawable ());

    titleTable = new Table ();
    titleTable.setBackground (widgetFactory.createIntelBoxTitleBackgroundDrawable ());
    titleTable.add (titleLabel).padLeft (16).expand ().fill ();
    intelBoxTable.add (titleTable).height (40).spaceBottom (10).fill ();

    intelBoxTable.row ().padLeft (16).spaceTop (10).spaceBottom (10).expandY ();

    playerNameTable = new Table ();
    playerNameTable.add (playerNameSettingLabel).fill ();
    playerColorIconCell = playerNameTable.add (playerColorIcon.asActor ()).spaceRight (4);
    playerNameTable.add (playerNameTextLabel).spaceLeft (4).expandX ().fill ();
    intelBoxTable.add (playerNameTable).padRight (4).expandX ().fill ();

    intelBoxTable.row ().padLeft (16).padRight (16).spaceTop (10).spaceBottom (10).expandY ();

    final Table serverNameTable = new Table ();
    serverNameTable.add (serverNameSettingLabel).fill ();
    serverNameTable.add (serverNameTextLabel).expandX ().fill ();
    intelBoxTable.add (serverNameTable).expandX ().fill ();

    intelBoxTable.row ().padLeft (16).padRight (16).spaceTop (10).spaceBottom (10).expandY ();

    final Table mapNameTable = new Table ();
    mapNameTable.add (mapNameSettingLabel).fill ();
    mapNameTable.add (mapNameTextLabel).expandX ().fill ();
    intelBoxTable.add (mapNameTable).expandX ().fill ();

    intelBoxTable.row ().padLeft (16).padRight (16).spaceTop (10).spaceBottom (10).expandY ();

    final Table gameRoundTable = new Table ();
    gameRoundTable.add (gameRoundSettingLabel).fill ();
    gameRoundTable.add (gameRoundTextLabel).expandX ().fill ();
    intelBoxTable.add (gameRoundTable).expandX ().fill ();

    intelBoxTable.row ().padLeft (16).padRight (16).spaceTop (10).spaceBottom (10).expandY ();

    final Table gamePhaseTable = new Table ();
    gamePhaseTable.add (gamePhaseSettingLabel).fill ();
    gamePhaseTable.add (gamePhaseTextLabel).expandX ().fill ();
    intelBoxTable.add (gamePhaseTable).expandX ().fill ();

    intelBoxTable.row ().padLeft (16).padRight (16).spaceTop (10).spaceBottom (10).expandY ();

    final Table conquerTable = new Table ();
    conquerTable.add (conquerSettingLabel).fill ();
    conquerTable.add (conquerWinPercentTextLabel).expandX ().fill ();
    conquerTable.row ().padLeft (10);
    conquerTable.add (conquerCountryCountTextLabel).expandX ().fill ().colspan (2);
    intelBoxTable.add (conquerTable).expandX ().fill ();

    intelBoxTable.row ().padLeft (16).padRight (16).spaceTop (10).expandY ();

    final Table reinforcementsTable = new Table ();
    reinforcementsTable.add (reinforcementsSettingLabel).expandX ().fill ().colspan (3);
    reinforcementsTable.row ();
    reinforcementsTable.add (countryReinforcementsSettingLabel).padLeft (10).fill ();
    reinforcementsTable.add (countryReinforcementsTextLabel).fill ();
    reinforcementsTable.add ().expandX ();
    reinforcementsTable.row ();
    reinforcementsTable.add (continentReinforcementsSettingLabel).padLeft (10).fill ();
    reinforcementsTable.add (continentReinforcementsTextLabel).fill ();
    reinforcementsTable.add ().expandX ();
    reinforcementsTable.row ();
    reinforcementsTable.add (subtotalReinforcementsSettingLabel).padLeft (10).fill ();
    reinforcementsTable.add (subtotalReinforcementsTextLabel).fill ();
    reinforcementsTable.add ().expandX ();
    reinforcementsTable.row ();
    reinforcementsTable.add (tradeInReinforcementsSettingLabel).padLeft (10).fill ();
    reinforcementsTable.add (tradeInReinforcementsTextLabel).fill ();
    reinforcementsTable.add ().expandX ();
    reinforcementsTable.row ();
    reinforcementsTable.add (totalReinforcementsSettingLabel).padLeft (10).fill ();
    reinforcementsTable.add (totalReinforcementsTextLabel).fill ();
    reinforcementsTable.add ().expandX ();
    intelBoxTable.add (reinforcementsTable).expandX ().fill ();

    intelBoxTable.row ().padLeft (16).padRight (16).padBottom (11).spaceTop (10).expandY ();

    final Table detailedReportTable = new Table ();
    detailedReportTable.add (detailedReportButton).spaceRight (10).fill ();
    detailedReportTable.add (detailedReportButtonLabel).spaceLeft (10).expandX ().fill ();
    intelBoxTable.add (detailedReportTable).expandX ().fill ();
  }

  @Override
  public void setSelfPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    selfPlayer = player;

    playerNameTextLabel.setText (player.getName ());
    playerColorIcon = widgetFactory.createPlayerColorIcon (player);
    playerColorIconCell.setActor (playerColorIcon.asActor ());
    playerNameTable.invalidateHierarchy ();
  }

  @Override
  public void setGameServerConfiguration (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    gameServerConfig = config;

    setMapMetadata (config.getMapMetadata ());
    setServerName (config.getGameServerName ());
    setWinConditions (ownedCountries, config);
  }

  @Override
  public void setClientConfiguration (final ClientConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");
  }

  @Override
  public void setMapMetadata (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    mapNameTextLabel.setText (Strings.toProperCase (mapMetadata.getName ()));
  }

  @Override
  public void setGamePhaseName (final String phaseName)
  {
    Arguments.checkIsNotNull (phaseName, "phaseName");

    gamePhaseTextLabel.setText (phaseName);
  }

  @Override
  public void setGameRound (final int round)
  {
    Arguments.checkIsNotNegative (round, "round");

    gameRoundTextLabel.setText (String.valueOf (round));
  }

  @Override
  public void setOwnedCountriesForSelf (final int countries, @Nullable final PlayerPacket player)
  {
    Arguments.checkIsNotNegative (countries, "countries");

    if (gameServerConfig == null || !isSelf (player)) return;

    ownedCountries = countries;

    setWinConditions (countries, gameServerConfig);
  }

  @Override
  public void addOwnedCountryForSelf (@Nullable final PlayerPacket player)
  {
    if (gameServerConfig == null || !isSelf (player)) return;

    setWinConditions (++ownedCountries, gameServerConfig);
  }

  @Override
  public void removeOwnedCountryForSelf (@Nullable final PlayerPacket player)
  {
    if (gameServerConfig == null || !isSelf (player)) return;

    setWinConditions (--ownedCountries, gameServerConfig);
  }

  @Override
  public void clear ()
  {
    playerNameTextLabel.setText ("?");
    serverNameTextLabel.setText ("?");
    mapNameTextLabel.setText ("?");
    gameRoundTextLabel.setText ("?");
    gamePhaseTextLabel.setText ("?");
    conquerWinPercentTextLabel.setText ("? of ? %");
    conquerCountryCountTextLabel.setText ("* Conquer ? more countries to win");
    countryReinforcementsTextLabel.setText ("?");
    continentReinforcementsTextLabel.setText ("?");
    subtotalReinforcementsTextLabel.setText ("?");
    tradeInReinforcementsTextLabel.setText ("?");
    totalReinforcementsTextLabel.setText ("?");
    playerColorIcon = PlayerColorIcon.NULL_PLAYER_COLOR_ICON;
    playerColorIconCell.setActor (playerColorIcon.asActor ());
    playerNameTable.invalidateHierarchy ();
  }

  @Override
  public Actor asActor ()
  {
    return intelBoxTable;
  }

  @Override
  public void refreshAssets ()
  {
    intelBoxTable.setBackground (widgetFactory.createIntelBoxBackgroundDrawable ());
    titleTable.setBackground (widgetFactory.createIntelBoxTitleBackgroundDrawable ());
    titleLabel.setStyle (widgetFactory.createIntelBoxTitleLabelStyle ());
    playerNameSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    playerNameTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextWrappingLabelStyle ());
    serverNameSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    serverNameTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextWrappingLabelStyle ());
    mapNameSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    mapNameTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextWrappingLabelStyle ());
    gameRoundSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    gameRoundTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    gamePhaseSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    gamePhaseTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    conquerSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    conquerWinPercentTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    conquerCountryCountTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    reinforcementsSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    countryReinforcementsSettingLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    countryReinforcementsTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    continentReinforcementsSettingLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    continentReinforcementsTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    subtotalReinforcementsSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    subtotalReinforcementsTextLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    tradeInReinforcementsSettingLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    tradeInReinforcementsTextLabel.setStyle (widgetFactory.createIntelBoxSettingTextLabelStyle ());
    totalReinforcementsSettingLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    totalReinforcementsTextLabel.setStyle (widgetFactory.createIntelBoxSettingNameLabelStyle ());
    detailedReportButtonLabel.setStyle (widgetFactory.createIntelBoxButtonTextLabelStyle ());
    detailedReportButton.setStyle (widgetFactory.createIntelBoxDetailedReportButtonStyle ());
    playerColorIcon.refreshAssets ();
  }

  private boolean isSelf (@Nullable final PlayerPacket player)
  {
    return selfPlayer != null && player != null && player.is (selfPlayer);
  }

  private void setServerName (final String serverName)
  {
    serverNameTextLabel.setText (serverName);
  }

  private void setWinConditions (final int ownedCountryCount, final GameServerConfiguration config)
  {
    final int currentWinPercent = Math.round (ownedCountryCount / (float) config.getTotalCountryCount () * 100.0f);
    final int nMoreCountriesToWin = config.getWinningCountryCount () - ownedCountryCount;

    conquerWinPercentTextLabel.setText (Strings.format ("{} of {} %", currentWinPercent, config.getWinPercentage ()));
    conquerCountryCountTextLabel.setText (Strings.format ("* Conquer {} to win", Strings
            .pluralize (nMoreCountriesToWin, "more country", "more countries")));
  }
}
