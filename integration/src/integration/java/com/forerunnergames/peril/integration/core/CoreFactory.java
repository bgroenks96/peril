package com.forerunnergames.peril.integration.core;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CoreFactory
{
  public static StateMachineEventHandler createDefaultGameStateMachine ()
  {
    return createGameStateMachine (new GameStateMachineConfig ());
  }

  public static StateMachineEventHandler createGameStateMachine (final GameStateMachineConfig config)
  {
    Arguments.checkIsNotNull (config, "config");

    final GameModel gameModel = config.getGameModel ();
    return new StateMachineEventHandler (gameModel);
  }

  public static CountryFactory generateTestCountries (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    final CountryFactory countryFactory = new CountryFactory ();
    for (int i = 0; i < count; ++i)
    {
      countryFactory.newCountryWith ("TestCountry-" + i);
    }
    return countryFactory;
  }

  public static final class GameStateMachineConfig
  {
    private GameModel gameModel;

    public GameStateMachineConfig setGameModel (final GameModel gameModel)
    {
      Arguments.checkIsNotNull (gameModel, "gameModel");

      this.gameModel = gameModel;
      return this;
    }

    public GameModel getGameModel ()
    {
      return gameModel;
    }
  }

  private CoreFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
