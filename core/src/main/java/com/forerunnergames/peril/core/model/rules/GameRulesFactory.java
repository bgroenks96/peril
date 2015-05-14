package com.forerunnergames.peril.core.model.rules;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import javax.annotation.Nullable;

public final class GameRulesFactory
{
  public static GameRules create (final GameConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    return create (config.getGameMode (), config.getPlayerLimit (), config.getWinPercentage (),
                   config.getTotalCountryCount (), config.getInitialCountryAssignment ());
  }

  public static GameRules create (final GameMode gameMode,
                                  @Nullable final Integer playerLimit,
                                  @Nullable final Integer winPercentage,
                                  @Nullable final Integer totalCountryCount,
                                  @Nullable final InitialCountryAssignment initialCountryAssignment)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    switch (gameMode)
    {
      case CLASSIC:
      {
        return new ClassicGameRules.Builder ().playerLimit (playerLimit).winPercentage (winPercentage)
                .totalCountryCount (totalCountryCount).initialCountryAssignment (initialCountryAssignment).build ();
      }
      case CUSTOM:
      {
        throw new UnsupportedOperationException ("Custom game mode is not supported yet!");
      }
      default:
      {
        throw new IllegalStateException ("Unknown game mode");
      }
    }
  }

  private GameRulesFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
