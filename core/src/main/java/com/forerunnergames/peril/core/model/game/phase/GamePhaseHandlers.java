package com.forerunnergames.peril.core.model.game.phase;

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

public class GamePhaseHandlers
{
  private final InitialPhaseHandler initialPhaseHandler;
  private final TurnPhaseHandler turnPhaseHandler;
  private final ReinforcementPhaseHandler reinforcePhaseHandler;
  private final AttackPhaseHandler attackPhaseHandler;
  private final FortifyPhaseHandler fortifyPhaseHandler;

  public GamePhaseHandlers (final InitialPhaseHandler initialPhaseHandler,
                            final TurnPhaseHandler turnPhaseHandler,
                            final ReinforcementPhaseHandler reinforcePhaseHandler,
                            final AttackPhaseHandler attackPhaseHandler,
                            final FortifyPhaseHandler fortifyPhaseHandler)
  {
    this.initialPhaseHandler = initialPhaseHandler;
    this.turnPhaseHandler = turnPhaseHandler;
    this.reinforcePhaseHandler = reinforcePhaseHandler;
    this.attackPhaseHandler = attackPhaseHandler;
    this.fortifyPhaseHandler = fortifyPhaseHandler;
  }

  public static GamePhaseHandlers createDefault (final GameModelConfiguration gameModelConfig)
  {
    final GamePhaseEventFactory defaultEventFactory = new DefaultGamePhaseEventFactory (
            gameModelConfig.getPlayerModel (), gameModelConfig.getPlayMapModel (), gameModelConfig.getCardModel (),
            gameModelConfig.getRules ());
    final TurnPhaseHandler defaultTurnPhaseHandler = new DefaultTurnPhaseHandler (gameModelConfig, defaultEventFactory);
    return new GamePhaseHandlers (new DefaultInitialPhaseHandler (gameModelConfig), defaultTurnPhaseHandler,
            new DefaultReinforcementPhaseHandler (gameModelConfig, defaultTurnPhaseHandler, defaultEventFactory),
            new DefaultAttackPhaseHandler (gameModelConfig), new DefaultFortifyPhaseHandler (gameModelConfig));
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
