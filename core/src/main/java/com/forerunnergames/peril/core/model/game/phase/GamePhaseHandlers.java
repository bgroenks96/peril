/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.game.phase;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.core.model.game.DefaultGamePhaseEventFactory;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.GamePhaseEventFactory;
import com.forerunnergames.peril.core.model.game.phase.init.DefaultInitialPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.init.InitialPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.AttackPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.DefaultAttackPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.DefaultFortifyPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.DefaultReinforcementPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.DefaultTurnPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.FortifyPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.ReinforcementPhaseHandler;
import com.forerunnergames.peril.core.model.game.phase.turn.TurnPhaseHandler;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableList;

import java.util.Stack;

public class GamePhaseHandlers
{
  private static GamePhaseHandlers instance;
  private final InitialPhaseHandler initialPhaseHandler;
  private final TurnPhaseHandler turnPhaseHandler;
  private final ReinforcementPhaseHandler reinforcePhaseHandler;
  private final AttackPhaseHandler attackPhaseHandler;
  private final FortifyPhaseHandler fortifyPhaseHandler;
  private final Stack <GamePhaseHandler> handlers = new Stack <> ();

  public GamePhaseHandlers (final InitialPhaseHandler initialPhaseHandler,
                            final TurnPhaseHandler turnPhaseHandler,
                            final ReinforcementPhaseHandler reinforcePhaseHandler,
                            final AttackPhaseHandler attackPhaseHandler,
                            final FortifyPhaseHandler fortifyPhaseHandler)
  {
    Arguments.checkIsNotNull (initialPhaseHandler, "initialPhaseHandler");
    Arguments.checkIsNotNull (turnPhaseHandler, "turnPhaseHandler");
    Arguments.checkIsNotNull (reinforcePhaseHandler, "reinforcePhaseHandler");
    Arguments.checkIsNotNull (attackPhaseHandler, "attackPhaseHandler");
    Arguments.checkIsNotNull (fortifyPhaseHandler, "fortifyPhaseHandler");

    this.initialPhaseHandler = initialPhaseHandler;
    this.turnPhaseHandler = turnPhaseHandler;
    this.reinforcePhaseHandler = reinforcePhaseHandler;
    this.attackPhaseHandler = attackPhaseHandler;
    this.fortifyPhaseHandler = fortifyPhaseHandler;
  }

  public static GamePhaseHandlers createDefault (final GameModelConfiguration gameModelConfig)
  {
    Arguments.checkIsNotNull (gameModelConfig, "gameModelConfig");

    final GamePhaseEventFactory defaultEventFactory = new DefaultGamePhaseEventFactory (
            gameModelConfig.getPlayerModel (), gameModelConfig.getPlayMapModel (), gameModelConfig.getCardModel (),
            gameModelConfig.getRules ());
    final TurnPhaseHandler defaultTurnPhaseHandler = new DefaultTurnPhaseHandler (gameModelConfig, defaultEventFactory);
    instance = new GamePhaseHandlers (new DefaultInitialPhaseHandler (gameModelConfig), defaultTurnPhaseHandler,
            new DefaultReinforcementPhaseHandler (gameModelConfig, defaultTurnPhaseHandler),
            new DefaultAttackPhaseHandler (gameModelConfig), new DefaultFortifyPhaseHandler (gameModelConfig));
    return instance;
  }

  public static GamePhaseHandlers get ()
  {
    Preconditions.checkIsTrue (instance != null, Strings.format ("{} instance has not yet been created!",
                                                                 GamePhaseHandlers.class.getSimpleName ()));

    return instance;
  }

  public static GamePhase getCurrentGamePhase ()
  {
    return getCurrentHandler ().getCurrentGamePhase ();
  }

  public static GamePhaseHandler getCurrentHandler ()
  {
    //@formatter:off
    Preconditions.checkIsTrue (instance != null, Strings.format ("{} instance has not yet been created!",
                                                                 GamePhaseHandlers.class.getSimpleName ()));
    Preconditions.checkIsFalse (instance.handlers.isEmpty (),
                           Strings.format ("No handlers have been activated! [{}]", instance.handlers.toString ()));
    //@formatter:on

    return instance.handlers.peek ();
  }

  public static ImmutableList <GamePhaseHandler> getAllActiveHandlers ()
  {
    Preconditions.checkIsTrue (instance != null, Strings.format ("{} instance has not yet been created!",
                                                                 GamePhaseHandlers.class.getSimpleName ()));

    return ImmutableList.copyOf (instance.handlers);
  }

  static void onGamePhaseBegin (final GamePhaseHandler handler)
  {
    Arguments.checkIsNotNull (handler, "handler");
    Preconditions.checkIsTrue (instance != null, Strings.format ("{} instance has not yet been created!",
                                                                 GamePhaseHandlers.class.getSimpleName ()));

    instance.handlers.push (handler);
  }

  static void onGamePhaseEnd (final GamePhaseHandler handler)
  {
    Arguments.checkIsNotNull (handler, "handler");
    Preconditions.checkIsTrue (instance != null, Strings.format ("{} instance has not yet been created!",
                                                                 GamePhaseHandlers.class.getSimpleName ()));
    Preconditions.checkIsTrue (handler.equals (getCurrentHandler ()),
                               Strings.format ("{} is not the current game phase handler!", handler));

    instance.handlers.pop ();
  }

  public InitialPhaseHandler getInitialPhaseHandler ()
  {
    return initialPhaseHandler;
  }

  public TurnPhaseHandler getTurnPhaseHandler ()
  {
    return turnPhaseHandler;
  }

  public ReinforcementPhaseHandler getReinforcePhaseHandler ()
  {
    return reinforcePhaseHandler;
  }

  public AttackPhaseHandler getAttackPhaseHandler ()
  {
    return attackPhaseHandler;
  }

  public FortifyPhaseHandler getFortifyPhaseHandler ()
  {
    return fortifyPhaseHandler;
  }
}
