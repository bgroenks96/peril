package com.forerunnergames.peril.integration.core;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.state.GameStateMachine;
import com.forerunnergames.peril.core.model.state.GameStateMachineListener;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public final class CoreFactory
{
  public static GameStateMachine createDefaultGameStateMachine ()
  {
    return createGameStateMachine (new GameStateMachineConfig ());
  }

  public static GameStateMachine createGameStateMachine (final GameStateMachineConfig config)
  {
    Arguments.checkIsNotNull (config, "config");

    final GameModel gameModel = config.getGameModel ();
    final GameStateMachineListener listener = config.getGameStateMachineListener ();
    return new GameStateMachine (gameModel, listener);
  }

  public static ImmutableSet <Country> generateTestCountries (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    final Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; ++i)
    {
      final Country country = CountryFactory.create ("Country-" + i);
      countrySetBuilder.add (country);
    }
    return countrySetBuilder.build ();
  }

  public static final class GameStateMachineConfig
  {
    private GameModel gameModel;
    private GameStateMachineListener listener = new GameStateMachineListener ()
    {
      @Override
      public void onEnd ()
      {
      }
    };

    public GameStateMachineConfig setGameModel (final GameModel gameModel)
    {
      Arguments.checkIsNotNull (gameModel, "gameModel");

      this.gameModel = gameModel;
      return this;
    }

    public GameStateMachineConfig setGameStateMachineListener (final GameStateMachineListener listener)
    {
      Arguments.checkIsNotNull (listener, "listener");

      this.listener = listener;
      return this;
    }

    public GameModel getGameModel ()
    {
      return gameModel;
    }

    public GameStateMachineListener getGameStateMachineListener ()
    {
      return listener;
    }
  }

  private CoreFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
