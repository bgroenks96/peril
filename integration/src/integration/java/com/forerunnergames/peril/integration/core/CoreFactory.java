package com.forerunnergames.peril.integration.core;

import com.forerunnergames.peril.core.model.StateMachineActionHandler;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public final class CoreFactory
{
  public static StateMachineEventHandler createDefaultGameStateMachine ()
  {
    return createGameStateMachine (new GameStateMachineConfig ());
  }

  public static StateMachineEventHandler createGameStateMachine (final GameStateMachineConfig config)
  {
    Arguments.checkIsNotNull (config, "config");

    final StateMachineActionHandler gameModel = config.getGameModel ();
    return new StateMachineEventHandler (gameModel);
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
    private StateMachineActionHandler gameModel;

    public GameStateMachineConfig setGameModel (final StateMachineActionHandler gameModel)
    {
      Arguments.checkIsNotNull (gameModel, "gameModel");

      this.gameModel = gameModel;
      return this;
    }

    public StateMachineActionHandler getGameModel ()
    {
      return gameModel;
    }
  }

  private CoreFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
