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

package com.forerunnergames.peril.integration.core;

import com.forerunnergames.peril.core.model.game.GameModel;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.phase.GamePhaseHandlers;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CoreFactory
{
  public static GameStateMachineConfig createDefaultConfigurationFrom (final GameModel gameModel)
  {
    final GameModelConfiguration gameModelConfig = gameModel.getConfiguration ();
    return new GameStateMachineConfig (gameModel, GamePhaseHandlers.createDefault (gameModelConfig));
  }

  public static StateMachineEventHandler createGameStateMachine (final GameStateMachineConfig config)
  {
    Arguments.checkIsNotNull (config, "config");

    return new StateMachineEventHandler (config.gameModel, config.gamePhaseHandlers);
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

  private CoreFactory ()
  {
    Classes.instantiationNotAllowed ();
  }

  public static final class GameStateMachineConfig
  {
    private final GameModel gameModel;
    private final GamePhaseHandlers gamePhaseHandlers;

    public GameStateMachineConfig (final GameModel gameModel, final GamePhaseHandlers gamePhaseHandlers)
    {
      this.gameModel = gameModel;
      this.gamePhaseHandlers = gamePhaseHandlers;
    }

    public GameModel getGameModel ()
    {
      return gameModel;
    }

    public GamePhaseHandlers getGamePhaseHandlers ()
    {
      return gamePhaseHandlers;
    }
  }
}
