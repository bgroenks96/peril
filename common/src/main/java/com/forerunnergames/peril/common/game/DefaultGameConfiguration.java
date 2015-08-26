package com.forerunnergames.peril.common.game;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultGameConfiguration implements GameConfiguration
{
  private final GameMode gameMode;
  private final int playerLimit;
  private final int winPercentage;
  private final InitialCountryAssignment initialCountryAssignment;
  private final MapMetadata mapMetadata;

  public DefaultGameConfiguration (final GameMode gameMode,
                                   final int playerLimit,
                                   final int winPercentage,
                                   final InitialCountryAssignment initialCountryAssignment,
                                   final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");
    Arguments.checkIsNotNegative (winPercentage, "winPercentage");
    Arguments.checkIsNotNull (initialCountryAssignment, "initialCountryAssignment");
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    this.gameMode = gameMode;
    this.playerLimit = playerLimit;
    this.winPercentage = winPercentage;
    this.initialCountryAssignment = initialCountryAssignment;
    this.mapMetadata = mapMetadata;
  }

  @Override
  public GameMode getGameMode ()
  {
    return gameMode;
  }

  @Override
  public int getPlayerLimit ()
  {
    return playerLimit;
  }

  @Override
  public int getWinPercentage ()
  {
    return winPercentage;
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return initialCountryAssignment;
  }

  @Override
  public String getMapName ()
  {
    return mapMetadata.getName ();
  }

  @Override
  public MapMetadata getMapMetadata ()
  {
    return mapMetadata;
  }

  @Override
  public MapType getMapType ()
  {
    return mapMetadata.getType ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Game mode: {} | Player limit: {} | Win Percentage: {}"
                                   + " | Initial Country Assignment: {} | Map: {}",
                           getClass ().getSimpleName (), gameMode, playerLimit, winPercentage, initialCountryAssignment,
                           mapMetadata);
  }

  @RequiredForNetworkSerialization
  private DefaultGameConfiguration ()
  {
    gameMode = null;
    playerLimit = 0;
    winPercentage = 0;
    initialCountryAssignment = null;
    mapMetadata = null;
  }
}
