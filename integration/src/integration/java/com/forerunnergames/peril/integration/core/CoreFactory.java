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

  private CoreFactory ()
  {
    Classes.instantiationNotAllowed ();
  }

  public static final class GameStateMachineConfig
  {
    private GameModel gameModel;

    public GameModel getGameModel ()
    {
      return gameModel;
    }

    public GameStateMachineConfig setGameModel (final GameModel gameModel)
    {
      Arguments.checkIsNotNull (gameModel, "gameModel");

      this.gameModel = gameModel;
      return this;
    }
  }
}
